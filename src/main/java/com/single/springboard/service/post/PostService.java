package com.single.springboard.service.post;

import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.comment.CommentRepository;
import com.single.springboard.domain.file.File;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.domain.post.dto.PostPaginationDto;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.file.FileService;
import com.single.springboard.service.post.dto.CountResponse;
import com.single.springboard.service.post.dto.PostRankResponse;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.util.CommentUtils;
import com.single.springboard.util.DateUtils;
import com.single.springboard.util.PostUtils;
import com.single.springboard.web.dto.comment.CommentsResponse;
import com.single.springboard.web.dto.post.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.single.springboard.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final FileService fileService;
    private final CommentUtils commentUtils;
    private final PostUtils postUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RANKING_KEY = "ranking";
    private static final String USER_VIEW_KEY = "post:view:user:";

    @Transactional
    public void savePostAndFiles(PostSaveRequest requestDto, SessionUser currentUser) {
        if (currentUser == null) {
            throw new CustomException(UNAUTHORIZED_USER_REQUIRED_LOGIN);
        }

        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Post post = postRepository.save(requestDto.toEntity(user));

        if (requestDto.files() != null) {
            fileService.postFilesSave(post, requestDto.files());
        }
    }

    public PostResponse findPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        return PostResponse.builder()
                .id(postId)
                .files(post.getFiles())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getName())
                .build();
    }

    @Transactional(readOnly = true)
    public PostElementsResponse findPostAndElements(Long id, @LoginUser SessionUser user) {
        Post post = postRepository.findPostWithCommentsAndUser(id);

        if (user != null) {
            increasePostViewCount(String.valueOf(id), user.getEmail(), post);
        }

        List<Comment> comments = post.getComments();
        List<Comment> sortedComments = commentUtils.commentsSort(comments);

        List<CommentsResponse> commentsResponses = sortedComments.stream()
                .map(comment -> CommentsResponse.builder()
                        .id(comment.getId())
                        .commentLevel(comment.getCommentLevel())
                        .parentId(comment.getParentComment())
                        .content(comment.isSecret() ?
                                (user != null && commentUtils.enableSecretCommentView(post.getUser().getName(),
                                        user.getName(), comment.getUser().getName()) ?
                                        comment.getContent() : "비밀 댓글 입니다.") : comment.getContent())
                        .author(comment.getUser().getName())
                        .createdDate(DateUtils.formatDate(comment.getCreatedDate()))
                        .build())
                .collect(Collectors.toList());

        return PostElementsResponse.builder()
                .id(post.getId())
                .author(post.getUser().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .comments(commentsResponses)
                .fileName(post.getFiles().stream().
                        map(File::getTranslateName)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PostsResponse> findAllPostsAndCommentsCountDesc(Pageable pageable) {
        return postRepository.findAllPostsWithCommentsCountAndUser(pageable)
                .map(objects -> {
                    Post post = (Post) objects[0];
                    Long commentsCount = (Long) objects[1];

                    return PostsResponse.builder()
                            .id(post.getId())
                            .author(post.getUser().getName())
                            .title(post.getTitle())
                            .modifiedDate(DateUtils.formatDate(post.getModifiedDate()))
                            .viewCount(post.getViewCount())
                            .commentsCount(commentsCount)
                            .build();
                });
    }

    @Transactional
    public void updatePost(Long id, PostUpdateRequest updateDto) {
        Post post = postRepository.findPostWithFiles(id);

        if (post == null) {
            throw new CustomException(NOT_FOUND_POST);
        }

        List<File> existFiles = post.getFiles();

        fileService.postFilesUpdate(post, existFiles, updateDto.files(),
                postUtils.parseJsonStringToMap(updateDto.oldFileNames()));

        post.updatePost(updateDto);
    }


    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        if (post.getFiles().size() > 0) {
            fileService.deletePostChildFiles(post);
        }

        postRepository.deleteById(post.getId());
        redisTemplate.opsForZSet().remove(RANKING_KEY, post.getTitle() + ":" + post.getId());
    }

    public List<PostRankResponse> getPostsRanking() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples =
                zSetOperations.reverseRangeWithScores(RANKING_KEY, 0, 4);

        List<PostRankResponse> responses = new ArrayList<>();
        if (typedTuples != null) {
            List<PostRankResponse> list = typedTuples.stream()
                    .map(tuple -> {
                        String[] splitValue = String.valueOf(tuple.getValue()).split(":");
                        return PostRankResponse.builder()
                                .id(Long.parseLong(splitValue[splitValue.length - 1]))
                                .title(splitValue[0])
                                .score(tuple.getScore().longValue())
                                .build();
                    })
                    .toList();

            responses.addAll(list);
        }

        return responses;
    }

    public void increasePostViewCount(String postId, String userId, Post post) {
        String userViewKey = USER_VIEW_KEY + userId;

        boolean hasViewed = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userViewKey, postId));

        if (!hasViewed) {
            redisTemplate.opsForZSet().incrementScore(RANKING_KEY, post.getTitle() + ":" + postId, 1);
            redisTemplate.opsForSet().add(userViewKey, postId);
            post.increaseViewCount();
        }
    }

    public CountResponse countPostAndComment(SessionUser user) {
        Long postCount = postRepository.countPostByUser(user.getName());
        Long commentCount = commentRepository.countCommentByUser(user.getName());

        return new CountResponse(postCount, commentCount);
    }

    public List<PostPaginationDto> findWrittenPostByUsername(SessionUser user, Long postId) {
        return postRepository.postPagination(postId, user.getName(), 10);
    }
}

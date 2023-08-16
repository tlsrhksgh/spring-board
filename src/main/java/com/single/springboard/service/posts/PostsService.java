package com.single.springboard.service.posts;

import com.single.springboard.config.auth.LoginUser;
import com.single.springboard.config.auth.dto.SessionUser;
import com.single.springboard.domain.comments.Comments;
import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.files.FilesService;
import com.single.springboard.util.CommentsUtils;
import com.single.springboard.util.DateUtils;
import com.single.springboard.web.dto.comments.CommentsResponse;
import com.single.springboard.web.dto.posts.PostResponse;
import com.single.springboard.web.dto.posts.PostSaveRequest;
import com.single.springboard.web.dto.posts.PostUpdateRequest;
import com.single.springboard.web.dto.posts.PostsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;
import static com.single.springboard.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final FilesService filesService;
    private final CommentsUtils commentsUtils;
    private final RedisTemplate<String, String> redisTemplate;

    public void increasePostViewCount(String postId, String userId) {
        String userViewKey = "post:view:user:" + userId;

        boolean hasViewed = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userViewKey, postId));

        if (!hasViewed) {
            Posts post = postsRepository.findById(Long.valueOf(postId))
                    .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

            redisTemplate.opsForZSet().incrementScore("ranking", post.getTitle() + ":" + postId, 1);
            redisTemplate.opsForSet().add(userViewKey, postId);
            post.updateViewCount();
        }
    }

    @Transactional
    public Long savePostAndFiles(PostSaveRequest requestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Long postId = postsRepository.save(requestDto.toEntity(user)).getId();

        if (requestDto.files() != null) {
            filesService.translateFileAndSave(postId, requestDto.files());
        }

        return postId;
    }

    public PostResponse findPostByIdAndComments(Long id, @LoginUser SessionUser user) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        if (user != null) {
            increasePostViewCount(String.valueOf(id), user.email());
        }

        List<Comments> comments = post.getComments();
        List<Comments> sortedComments = commentsUtils.commentsSort(comments);

        List<CommentsResponse> commentsResponses = sortedComments.stream()
                .map(comment -> CommentsResponse.builder()
                        .id(comment.getId())
                        .commentLevel(comment.getCommentLevel())
                        .parentId(comment.getParentComment())
                        .content(comment.isSecret() ?
                                (user != null && commentsUtils.enableSecretCommentView(post.getUser().getName(),
                                        user.name(), comment.getUser().getName()) ?
                                        comment.getContent() : "비밀 댓글 입니다.") : comment.getContent())
                        .author(comment.getUser().getName())
                        .createdDate(DateUtils.formatDate(comment.getCreatedDate()))
                        .build())
                .collect(Collectors.toList());

        return PostResponse.builder()
                .author(post.getUser().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .comments(commentsResponses)
                .fileName(post.getFiles().stream().
                        map(Files::getTranslateName)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PostsResponse> findAllPostsAndCommentsCountDesc(Pageable pageable) {
        return postsRepository.findAllPostsWithCommentsCount(pageable)
                .map(objects -> {
                    Posts post = (Posts) objects[0];
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
    public Long updatePost(Long id, PostUpdateRequest updateDto) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
        post.updatePost(updateDto);

        return post.getId();
    }


    @Transactional
    public boolean deletePostWithFiles(Long id) {
        Posts post = postsRepository.findById(id)
                .orElse(null);

        if (post != null) {
            filesService.deleteChildFiles(post);
            postsRepository.deleteById(post.getId());
            return true;
        }

        return false;
    }
}

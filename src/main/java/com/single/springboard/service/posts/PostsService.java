package com.single.springboard.service.posts;

import com.single.springboard.service.files.FilesService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.domain.comments.Comments;
import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.files.FilesRepository;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.files.AwsS3Upload;
import com.single.springboard.util.CommentsUtils;
import com.single.springboard.util.DateUtils;
import com.single.springboard.web.dto.comments.CommentsResponse;
import com.single.springboard.web.dto.posts.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;
import static com.single.springboard.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final FilesRepository filesRepository;
    private final FilesService filesService;
    private final AwsS3Upload awsS3Upload;
    private final CommentsUtils commentsUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Long savePostAndFiles(PostSaveRequest requestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Posts post = postsRepository.save(requestDto.toEntity(user));

        List<Files> files = new ArrayList<>();

        if (requestDto.files() != null) {
            files.addAll(filesService.postFilesSave(post, requestDto.files()));
        }

        return post.getId();
    }

    public PostResponse findPostById(Long postId) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        return PostResponse.builder()
                .id(postId)
                .files(post.getFiles())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getName())
                .build();
    }

    public PostElementsResponse findPostAndElements(Long id, @LoginUser SessionUser user) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        if (user != null) {
            increasePostViewCount(String.valueOf(id), user.getEmail());
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
        List<Files> existFiles = filesRepository.findAllByPostsId(id);
        if(existFiles.size() > 0) {
            awsS3Upload.delete(existFiles);
            filesRepository.deleteFiles(id);
        }

        if(updateDto.files() != null) {
            filesService.postFilesSave(post, updateDto.files());
        }

        return post.getId();
    }


    @Transactional
    public boolean deletePostWithFiles(Long id) {
        Posts post = postsRepository.findById(id)
                .orElse(null);

        if (post != null && post.getFiles().size() > 0) {
            filesService.deletePostChildFiles(post);
            postsRepository.deleteById(post.getId());
            return true;
        } else if (post != null){
            postsRepository.deleteById(post.getId());
            return true;
        }

        return false;
    }

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
}

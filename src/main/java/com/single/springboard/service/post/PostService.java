package com.single.springboard.service.post;

import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.post.Post;
import com.single.springboard.service.file.FileService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.domain.file.File;
import com.single.springboard.domain.file.FileRepository;
import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.file.AwsS3Upload;
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
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final AwsS3Upload awsS3Upload;
    private final CommentsUtils commentsUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Long savePostAndFiles(PostSaveRequest requestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Post post = postRepository.save(requestDto.toEntity(user));

        List<File> files = new ArrayList<>();

        if (requestDto.files() != null) {
            files.addAll(fileService.postFilesSave(post, requestDto.files()));
        }

        return post.getId();
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

    public PostElementsResponse findPostAndElements(Long id, @LoginUser SessionUser user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        if (user != null) {
            increasePostViewCount(String.valueOf(id), user.getEmail());
        }

        List<Comment> comments = post.getComments();
        List<Comment> sortedComments = commentsUtils.commentsSort(comments);

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
                        map(File::getTranslateName)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PostsResponse> findAllPostsAndCommentsCountDesc(Pageable pageable) {
        return postRepository.findAllPostsWithCommentsCount(pageable)
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
    public Long updatePost(Long id, PostUpdateRequest updateDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
        post.updatePost(updateDto);
        List<File> existFiles = fileRepository.findAllByPostId(id);
        if(existFiles.size() > 0) {
            awsS3Upload.delete(existFiles);
            fileRepository.deleteFiles(id);
        }

        if(updateDto.files() != null) {
            fileService.postFilesSave(post, updateDto.files());
        }

        return post.getId();
    }


    @Transactional
    public boolean deletePostWithFiles(Long id) {
        Post post = postRepository.findById(id)
                .orElse(null);

        if (post != null && post.getFiles().size() > 0) {
            fileService.deletePostChildFiles(post);
            postRepository.deleteById(post.getId());
            return true;
        } else if (post != null){
            postRepository.deleteById(post.getId());
            return true;
        }

        return false;
    }

    public void increasePostViewCount(String postId, String userId) {
        String userViewKey = "post:view:user:" + userId;

        boolean hasViewed = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userViewKey, postId));

        if (!hasViewed) {
            Post post = postRepository.findById(Long.valueOf(postId))
                    .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

            redisTemplate.opsForZSet().incrementScore("ranking", post.getTitle() + ":" + postId, 1);
            redisTemplate.opsForSet().add(userViewKey, postId);
            post.updateViewCount();
        }
    }
}

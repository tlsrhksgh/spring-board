package com.single.springboard.service.post;

import com.single.springboard.aop.MeasureExecutionTime;
import com.single.springboard.client.RedisClient;
import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.comment.CommentRepository;
import com.single.springboard.domain.file.File;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.domain.post.dto.MainPostList;
import com.single.springboard.domain.post.dto.PostListPaginationNoOffset;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.file.FileService;
import com.single.springboard.service.post.dto.CountResponse;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.util.CommentUtils;
import com.single.springboard.util.PostUtils;
import com.single.springboard.web.dto.comment.CommentsResponse;
import com.single.springboard.web.dto.post.PostDetailResponse;
import com.single.springboard.web.dto.post.PostResponse;
import com.single.springboard.web.dto.post.PostSaveRequest;
import com.single.springboard.web.dto.post.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private final RedisClient redisClient;

    @Transactional
    public void savePostAndFiles(PostSaveRequest requestDto, SessionUser currentUser) {
        if (ObjectUtils.isEmpty(currentUser)) {
            throw new CustomException(UNAUTHORIZED_USER_REQUIRED_LOGIN);
        }

        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Post post = postRepository.save(requestDto.toEntity(user));

        if (!ObjectUtils.isEmpty(requestDto.files())) {
            fileService.postFilesSave(post, requestDto.files());
        }
    }

    public PostResponse findPostById(Long postId, SessionUser user) {
        Post post = redisClient.get(postId, Post.class);

        if(ObjectUtils.isEmpty(post)) {
            post = postRepository.findById(postId)
                    .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
            redisClient.put(postId, post);
        }

        postUtils.checkPostAuthor(post, user);

        return PostResponse.builder()
                .id(postId)
                .files(post.getFiles())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .build();
    }

    @MeasureExecutionTime
    @Transactional(readOnly = true)
    public PostDetailResponse findPostDetail(Long postId, @LoginUser SessionUser user) {
        Post post = redisClient.get(postId, Post.class);

        if(ObjectUtils.isEmpty(post)) {
            post = postRepository.findById(postId)
                    .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
            redisClient.put(postId, post);
        }

        if (!ObjectUtils.isEmpty(user)) {
            redisClient.increasePostViewCount(user.getEmail(), post);
        }

        List<Comment> comments = post.getComments();
        List<Comment> sortedComments = commentUtils.commentsSort(comments);

        Post finalPost = post;
        List<CommentsResponse> commentsResponses = sortedComments.stream()
                .map(comment -> CommentsResponse.builder()
                        .id(comment.getId())
                        .commentLevel(comment.getCommentLevel())
                        .parentId(comment.getParentComment())
                        .content(comment.isSecret() ? commentUtils.enableSecretCommentView(finalPost.getAuthor(),
                                        user.getName(), comment.getAuthor()) ?
                                        comment.getContent() : "비밀 댓글 입니다." : comment.getContent())
                        .author(comment.getAuthor())
                        .createdDate(comment.getCreatedDate().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        ))
                        .build())
                .collect(Collectors.toList());

        return PostDetailResponse.builder()
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

    @MeasureExecutionTime
    @Transactional(readOnly = true)
    public Page<MainPostList> findAllPostAndCommentsCountDesc(Pageable pageable) {
        return postRepository.findAllPostWithCommentsCount(pageable);
    }

    @Transactional
    public void updatePost(Long id, PostUpdateRequest updateDto, SessionUser user) {
        Post post = postRepository.findPostWithFiles(id);

        postUtils.checkPostAuthor(post, user);

        List<File> existFiles = post.getFiles();

        fileService.postFilesUpdate(post, existFiles, updateDto.files(),
                postUtils.parseJsonStringToMap(updateDto.oldFileNames()));

        post.updatePost(updateDto);
        redisClient.put(id, post);
    }


    @Transactional
    public void deletePost(Long id, SessionUser user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        postUtils.checkPostAuthor(post, user);

        if (!post.getFiles().isEmpty()) {
            fileService.deletePostChildFiles(post);
        }

        postRepository.deleteById(post.getId());
        redisClient.delete(id);
    }

    @Transactional
    public void deleteAllPost(List<Long> ids, SessionUser user) {
        postRepository.deleteAllPostByIds(ids, user.getName());
        redisClient.delete(ids);
    }

    public CountResponse countPostAndComment(SessionUser user) {
        Long postCount = postRepository.countPostByUser(user.getName());
        Long commentCount = commentRepository.countCommentByUser(user.getName());

        return new CountResponse(postCount, commentCount);
    }

    @MeasureExecutionTime
    public List<PostListPaginationNoOffset> findWrittenPostByUsername(SessionUser user, Long postId) {
        return postRepository.postListPaginationNoOffset(postId, user.getName(), 10);
    }
}

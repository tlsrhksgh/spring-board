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
import com.single.springboard.util.CommonUtils;
import com.single.springboard.util.PostUtils;
import com.single.springboard.web.dto.comment.CommentsResponse;
import com.single.springboard.web.dto.post.PostDetailResponse;
import com.single.springboard.web.dto.post.PostResponse;
import com.single.springboard.web.dto.post.PostSaveRequest;
import com.single.springboard.web.dto.post.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.single.springboard.client.constants.PostKeys.POSTS_KEY;
import static com.single.springboard.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final FileService fileService;
    private final CommentUtils commentUtils;
    private final PostUtils postUtils;
    private final CommonUtils commonUtils;
    private final RedisClient redisClient;

    @Transactional
    public void savePostAndFiles(PostSaveRequest requestDto, SessionUser currentUser) {
        if (ObjectUtils.isEmpty(currentUser)) {
            throw new CustomException(USER_REQUIRED_LOGIN);
        }

        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Post post = postRepository.save(requestDto.toEntity(user));

        if (!ObjectUtils.isEmpty(requestDto.files())) {
            fileService.postFilesSave(post, requestDto.files());
        }
    }

    @Transactional(readOnly = true)
    public PostResponse findPostById(Long postId, SessionUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        commonUtils.authorVerification(post, user);

        return PostResponse.builder()
                .id(postId)
                .files(post.getFiles())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .build();
    }

    @Transactional
    @MeasureExecutionTime
    public PostDetailResponse findPostDetail(Long postId, @LoginUser SessionUser user) {
        PostDetailResponse cachePost = redisClient.get(postId, PostDetailResponse.class);

        if (Objects.nonNull(user)) {
            postUtils.increasePostViewCount(user, postId);
        }

        if (Objects.nonNull(cachePost)) {
            return cachePost;
        }

        Post post = postRepository.findPostDetail(postId);

        List<Comment> comments = post.getComments();
        List<CommentsResponse> sortedComments = commentUtils.createCommentsResponses(comments, post, user);

        PostDetailResponse postDetail = PostDetailResponse.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .title(post.getTitle())
                .content(post.getContent())
                .comments(sortedComments)
                .fileName(post.getFiles().stream().
                        map(File::getTranslateName)
                        .collect(Collectors.toList()))
                .build();

        redisClient.put(postId, postDetail);
        return postDetail;
    }

    @MeasureExecutionTime
    @Transactional(readOnly = true)
    public Page<MainPostList> findAllPostAndCommentsCountDesc(Pageable pageable) {
        return postRepository.findAllPosts(pageable);
    }

    @Transactional
    public void updatePost(Long id, PostUpdateRequest updateDto, SessionUser user) {
        Post post = postRepository.findPostWithFiles(id);

        commonUtils.authorVerification(post, user);

        List<File> existFiles = post.getFiles();

        fileService.postFilesUpdate(post, existFiles, updateDto.files(),
                postUtils.parseJsonStringToMap(updateDto.oldFileNames()));

        post.updatePost(updateDto);
        redisClient.delete(POSTS_KEY.getKey(), List.of(id));
    }

    @Async
    @Transactional
    public void deletePost(Long postId, SessionUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        commonUtils.authorVerification(post, user);

        if (!ObjectUtils.isEmpty(post.getFiles())) {
            fileService.deletePostChildFiles(post);
        }

        postRepository.deleteById(postId);
        redisClient.delete(POSTS_KEY.getKey(), List.of(postId));
    }

    @Async
    @Transactional
    public void deleteAllPost(List<Long> ids, SessionUser user) {
        postRepository.deleteAllPostByIds(ids, user.getName());
        redisClient.delete(POSTS_KEY.getKey(), ids);
    }

    public CountResponse countPostAndComment(SessionUser user) {
        Long postCount = postRepository.countPostByUser(user.getName());
        Long commentCount = commentRepository.countCommentByUser(user.getName());

        return new CountResponse(postCount, commentCount);
    }

    @Transactional(readOnly = true)
    @MeasureExecutionTime
    public List<PostListPaginationNoOffset> findWrittenPostByUsername(SessionUser user, Long postId) {
        return postRepository.postListPaginationNoOffset(postId, user.getName(), 10);
    }
}

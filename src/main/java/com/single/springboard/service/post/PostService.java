package com.single.springboard.service.post;

import com.single.springboard.aop.annotation.UserVerify;
import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.comment.CommentRepository;
import com.single.springboard.domain.file.File;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.domain.post.dto.MainPostListNoOffset;
import com.single.springboard.domain.post.dto.MainPostPagination;
import com.single.springboard.domain.post.dto.PostListPaginationNoOffset;
import com.single.springboard.domain.post.dto.PostsResponse;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.file.FileService;
import com.single.springboard.service.post.dto.CountResponse;
import com.single.springboard.service.post.dto.PostRankResponse;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.util.CommentUtils;
import com.single.springboard.util.PostUtils;
import com.single.springboard.web.dto.comment.CommentsResponse;
import com.single.springboard.web.dto.post.PostResponse;
import com.single.springboard.web.dto.post.PostSaveRequest;
import com.single.springboard.web.dto.post.PostUpdateRequest;
import com.single.springboard.web.dto.post.PostWithElementsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.single.springboard.exception.ErrorCode.*;
import static com.single.springboard.service.post.constants.PostKeys.*;

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

    private static final AtomicLong postTotalCount = new AtomicLong();
    private static final AtomicLong latestPostId = new AtomicLong();

    @Transactional
    public void savePostAndFiles(PostSaveRequest requestDto, SessionUser currentUser) {
        if (ObjectUtils.isEmpty(currentUser)) {
            throw new CustomException(UNAUTHORIZED_USER_REQUIRED_LOGIN);
        }

        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Post post = postRepository.save(requestDto.toEntity(user));
        postTotalCount.incrementAndGet();
        latestPostId.incrementAndGet();

//        for (int i = 0; i < 1; i++) {
//            postRepository.save(requestDto.toEntity(user));
//            postTotalCount.incrementAndGet();
//            latestPostId.incrementAndGet();
//        }

        if (!ObjectUtils.isEmpty(requestDto.files())) {
            fileService.postFilesSave(post, requestDto.files());
        }

        if(ObjectUtils.isEmpty(redisTemplate.opsForValue().get(POST_TOTAL_COUNT.getKey()))) {
            redisTemplate.opsForValue().set(POST_TOTAL_COUNT.getKey(), String.valueOf(postTotalCount.incrementAndGet()));
        } else {
            long getTotalCount = Long.parseLong(redisTemplate.opsForValue().get(POST_TOTAL_COUNT.getKey()).toString());

            if(getTotalCount != postTotalCount.get()) {
                Long totalCount = postRepository.countAllPost();
                postTotalCount.set(totalCount);
                redisTemplate.opsForValue().setIfPresent(POST_TOTAL_COUNT.getKey(),
                        String.valueOf(totalCount));
                return;
            }
            redisTemplate.opsForValue().setIfPresent(POST_TOTAL_COUNT.getKey(),
                    String.valueOf(postTotalCount.incrementAndGet()));
        }
    }

    public PostResponse findPostById(Long postId, SessionUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        postUtils.checkPostAuthor(post, user);

        return PostResponse.builder()
                .id(postId)
                .files(post.getFiles())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .build();
    }

    @Transactional(readOnly = true)
    public PostWithElementsResponse findPostDetail(Long id, @LoginUser SessionUser user) {
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
                                        user.getName(), comment.getAuthor()) ?
                                        comment.getContent() : "비밀 댓글 입니다.") : comment.getContent())
                        .author(comment.getAuthor())
                        .createdDate(comment.getCreatedDate().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        ))
                        .build())
                .collect(Collectors.toList());

        return PostWithElementsResponse.builder()
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
    public PostsResponse findAllPostAndCommentsCountDesc(int currentPage, int pageSize) {
        Long postId;

        if (currentPage == 1) {
            postId = null;
        } else {
            long term = Math.abs(latestPostId.get() - postTotalCount.get()) -
                    (postTotalCount.get() - ((long) (currentPage - 1) * pageSize));
            postId = term > 0 ? term : Math.abs(latestPostId.get() - postTotalCount.get()) +
                    (postTotalCount.get() - ((long) (currentPage - 1) * pageSize));
        }


        List<MainPostListNoOffset> mainPostListNoOffset = postRepository
                .findAllPostWithCommentsNoOffset(postId, pageSize);

        MainPostPagination mainPostPagination = MainPostPagination.builder()
                .currentPage(currentPage)
                .size(pageSize)
                .totalPage((long) Math.ceil((double) postTotalCount.get() / pageSize))
                .build();

        return new PostsResponse(mainPostListNoOffset, mainPostPagination);
    }

    @Transactional
    public void updatePost(Long id, PostUpdateRequest updateDto, SessionUser user) {
        Post post = postRepository.findPostWithFiles(id);

        postUtils.checkPostAuthor(post, user);

        List<File> existFiles = post.getFiles();

        fileService.postFilesUpdate(post, existFiles, updateDto.files(),
                postUtils.parseJsonStringToMap(updateDto.oldFileNames()));

        post.updatePost(updateDto);
    }


    @Transactional
    public void deletePost(Long id, SessionUser user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        postUtils.checkPostAuthor(post, user);

        if (!post.getFiles().isEmpty()) {
            fileService.deletePostChildFiles(post);
        }

        redisTemplate.opsForValue().setIfPresent(POST_TOTAL_COUNT.getKey(),
                String.valueOf(postTotalCount.decrementAndGet()));
        postRepository.deleteById(post.getId());
        latestPostId.set(postRepository.findMaxPostId());
        redisTemplate.opsForZSet().remove(RANKING.getKey(), post.getTitle() + ":" + post.getId());
    }

    @Transactional
    public void deleteAllPost(List<Long> ids, SessionUser user) {
        postRepository.deleteAllPostByIds(ids, user.getName());
        redisTemplate.opsForValue().setIfPresent(POST_TOTAL_COUNT.getKey(),
                String.valueOf(postTotalCount.decrementAndGet()));
    }

    public List<PostRankResponse> getPostsRanking() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples =
                zSetOperations.reverseRangeWithScores(RANKING.getKey(), 0, 4);

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
        String userViewKey = USER_VIEW.getKey() + userId;

        boolean hasViewed = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userViewKey, postId));

        if (!hasViewed) {
            redisTemplate.opsForZSet().incrementScore(RANKING.getKey(), post.getTitle() + ":" + postId, 1);
            redisTemplate.opsForSet().add(userViewKey, postId);
            post.increaseViewCount();
        }
    }

    public CountResponse countPostAndComment(SessionUser user) {
        Long postCount = postRepository.countPostByUser(user.getName());
        Long commentCount = commentRepository.countCommentByUser(user.getName());

        return new CountResponse(postCount, commentCount);
    }

    public List<PostListPaginationNoOffset> findWrittenPostByUsername(SessionUser user, Long postId) {
        long startTime = System.currentTimeMillis();
        List<PostListPaginationNoOffset> result = postRepository.postListPaginationNoOffset(postId, user.getName(), 10);
        long endTime = System.currentTimeMillis();
        System.out.println("total time: " + (endTime - startTime) + "ms");
        return result;
    }
}

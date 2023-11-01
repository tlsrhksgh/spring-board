package com.single.springboard.service.comment;

import com.single.springboard.client.RedisClient;
import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.comment.CommentRepository;
import com.single.springboard.domain.dto.comment.CommentPaginationDto;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.util.CommonUtils;
import com.single.springboard.service.dto.comment.CommentSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.single.springboard.client.constants.PostKeys.POSTS_KEY;
import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;
import static com.single.springboard.exception.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RedisClient redisClient;
    private final CommonUtils commonUtils;

    @Transactional
    public void commentSave(CommentSaveRequest commentSaveRequest, SessionUser currentUser) {
        Post post = postRepository.findById(commentSaveRequest.postId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Comment comment;

        if (Objects.isNull(commentSaveRequest.parentId())) {
            comment = Comment.builder()
                    .user(user)
                    .author(commentSaveRequest.author())
                    .content(commentSaveRequest.content())
                    .secret(commentSaveRequest.secret())
                    .post(post)
                    .parentComment(null)
                    .commentLevel(1)
                    .build();
        } else {
            Comment parent = post.getComments().stream()
                    .filter(c -> Objects.equals(c.getId(), commentSaveRequest.parentId()))
                    .findFirst()
                    .get();

            comment = Comment.builder()
                    .user(user)
                    .author(commentSaveRequest.author())
                    .content(commentSaveRequest.content())
                    .secret(commentSaveRequest.secret())
                    .post(post)
                    .parentComment(parent)
                    .commentLevel(parent.getCommentLevel() + 1)
                    .build();
        }

        commentRepository.save(comment);
        redisClient.delete(POSTS_KEY.getKey(), List.of(commentSaveRequest.postId()));
    }

    @Transactional
    public void deleteComment(Long commentId, SessionUser user) {
        Comment commentWithPost = commentRepository.getComment(commentId);
        commonUtils.authorVerification(commentWithPost, user);
        commentRepository.deleteById(commentId);
        redisClient.delete(POSTS_KEY.getKey(), List.of(commentWithPost.getPost().getId()));
    }

    public List<CommentPaginationDto> findWrittenCommentByUsername(SessionUser user, Long commentId) {
        return commentRepository.commentListPaginationNoOffset(commentId, user.getName(), 10);
    }
}

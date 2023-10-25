package com.single.springboard.service.comment;

import com.single.springboard.client.RedisClient;
import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.comment.CommentRepository;
import com.single.springboard.domain.comment.dto.CommentPaginationDto;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.comment.CommentSaveRequest;
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

    @Transactional
    public void commentSave(CommentSaveRequest requestDto, SessionUser currentUser) {
        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Comment comment;

        if (Objects.isNull(requestDto.parentId())) {
            comment = Comment.builder()
                    .user(user)
                    .author(requestDto.nickname())
                    .content(requestDto.content())
                    .secret(requestDto.secret())
                    .post(post)
                    .parentComment(null)
                    .commentLevel(1)
                    .build();
        } else {
            Comment parent = post.getComments().stream()
                    .filter(c -> Objects.equals(c.getId(), requestDto.parentId()))
                    .findFirst()
                    .get();

            comment = Comment.builder()
                    .user(user)
                    .author(requestDto.nickname())
                    .content(requestDto.content())
                    .secret(requestDto.secret())
                    .post(post)
                    .parentComment(parent)
                    .commentLevel(parent.getCommentLevel() + 1)
                    .build();
        }

        commentRepository.save(comment);
        redisClient.delete(POSTS_KEY.getKey(), List.of(requestDto.postId()));
    }

    public Long deleteOneComment(Long commentId) {
        commentRepository.deleteById(commentId);

        return commentId;
    }

    public List<CommentPaginationDto> findWrittenCommentByUsername(SessionUser user, Long commentId) {
        return commentRepository.commentListPaginationNoOffset(commentId, user.getName(), 10);
    }
}

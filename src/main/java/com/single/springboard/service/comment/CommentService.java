package com.single.springboard.service.comment;

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
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;
import static com.single.springboard.exception.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long commentSave(CommentSaveRequest requestDto, String email) {
        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Comment comment;

        if(ObjectUtils.isEmpty(requestDto.parentId())) {
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

        return commentRepository.save(comment).getId();
    }

    public Long deleteOneComment(Long commentId) {
        commentRepository.deleteById(commentId);

        return commentId;
    }

    public List<CommentPaginationDto> findWrittenCommentByUsername(SessionUser user, Long commentId) {
        return commentRepository.commentListPaginationNoOffset(commentId, user.getName(), 10);
    }
}

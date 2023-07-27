package com.single.springboard.service.posts;

import com.single.springboard.domain.comments.Comments;
import com.single.springboard.domain.comments.CommentsRepository;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.web.dto.comments.CommentSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;
import static com.single.springboard.exception.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
public class CommentsService {
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long commentSave(CommentSaveRequest requestDto, String email) {
        Posts post = postsRepository.findById(requestDto.postId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Comments parentComment = findParentComment(requestDto);

        Comments comment;

        if(parentComment == null) {
            comment = Comments.builder()
                    .user(user)
                    .content(requestDto.content())
                    .secret(requestDto.secret())
                    .posts(post)
                    .parentId(post.getId())
                    .replyLevel(1)
                    .build();
        } else {
            comment = Comments.builder()
                    .user(user)
                    .content(requestDto.content())
                    .secret(requestDto.secret())
                    .posts(post)
                    .parentId(requestDto.parentId())
                    .replyLevel(parentComment.getReplyLevel() + 1)
                    .build();
        }

        return commentsRepository.save(comment).getId();
    }

    private Comments findParentComment(CommentSaveRequest requestDto) {
        List<Comments> comments = commentsRepository.findAllByPostsId(requestDto.postId());
        Comments parentComment = comments.stream().filter(
                comment -> comment.getId() == requestDto.parentId())
                .findAny()
                .orElse(null);

        return parentComment;
    }

    public Long deleteComment(Long commentId) {
        commentsRepository.deleteById(commentId);
        return commentId;
    }
}
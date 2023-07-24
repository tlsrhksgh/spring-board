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

        Comments comment = Comments.builder()
                .user(user)
                .content(requestDto.content())
                .password(requestDto.password())
                .posts(post)
                .build();

        return commentsRepository.save(comment).getId();
    }
}

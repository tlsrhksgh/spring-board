package com.single.springboard.web;

import com.single.springboard.config.auth.LoginUser;
import com.single.springboard.config.auth.dto.SessionUser;
import com.single.springboard.service.posts.CommentsService;
import com.single.springboard.web.dto.comments.CommentSaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@RestController
public class CommentApiController {

    private final CommentsService commentsService;

    @PostMapping
    public ResponseEntity<Long> commentSave(@RequestBody @Valid CommentSaveRequest requestDto,
                                            @LoginUser SessionUser user) {
        return ResponseEntity.ok(commentsService.commentSave(requestDto, user.email()));
    }
}

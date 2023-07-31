package com.single.springboard.web;

import com.single.springboard.config.auth.LoginUser;
import com.single.springboard.config.auth.dto.SessionUser;
import com.single.springboard.service.posts.CommentsService;
import com.single.springboard.web.dto.comments.CommentSaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class CommentApiController {

    private final CommentsService commentsService;

    @PostMapping
    public ResponseEntity<Long> commentSave(@RequestBody @Valid CommentSaveRequest requestDto,
                                            @LoginUser SessionUser user) {
        return ResponseEntity.ok(commentsService.commentSave(requestDto, user.email()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> commentDelete(@PathVariable Long id, List<Long> commentsIds) {
        return ResponseEntity.ok(commentsService.deleteComment(id, commentsIds));
    }
}

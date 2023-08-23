package com.single.springboard.web;

import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.service.comments.CommentsService;
import com.single.springboard.web.dto.comments.CommentSaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class CommentApiController {

    private final CommentsService commentsService;

    @PostMapping
    public ResponseEntity<Long> commentSave(@RequestBody @Valid CommentSaveRequest requestDto,
                                            @LoginUser SessionUser user) {
        return ResponseEntity.ok(commentsService.commentSave(requestDto, user.getEmail()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> commentDelete(@PathVariable Long id) {
        return ResponseEntity.ok(commentsService.deleteOneComment(id));
    }
}

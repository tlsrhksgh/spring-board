package com.single.springboard.web;

import com.single.springboard.domain.dto.comment.CommentPaginationDto;
import com.single.springboard.service.comment.CommentService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.service.dto.comment.CommentSaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class CommentApiController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> commentSave(@RequestBody @Valid CommentSaveRequest requestDto,
                                                          @LoginUser SessionUser user) {
        commentService.commentSave(requestDto, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> commentDelete(@PathVariable Long id,
                                              @LoginUser SessionUser user) {
        commentService.deleteComment(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/comment-list")
    public ResponseEntity<List<CommentPaginationDto>> findCommentList(
            @LoginUser SessionUser user,
            @RequestParam(value = "commentId", required = false) Long commentId) {
        return ResponseEntity.ok(commentService.findWrittenCommentByUsername(user, commentId));
    }
}

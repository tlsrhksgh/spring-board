package com.single.springboard.web;

import com.single.springboard.service.post.PostService;
import com.single.springboard.service.post.dto.CountResponse;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.post.PostSaveRequest;
import com.single.springboard.web.dto.post.PostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostApiController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> savePost(
            @ModelAttribute @Valid PostSaveRequest requestDto,
            @LoginUser SessionUser user) {
        postService.savePostAndFiles(requestDto, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Long id, @ModelAttribute @Valid PostUpdateRequest updateDto) {
        postService.updatePost(id, updateDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/count")
    public ResponseEntity<CountResponse> CountPostAndComment(@LoginUser SessionUser user) {
        return ResponseEntity.ok(postService.countPostAndComment(user));
    }
}

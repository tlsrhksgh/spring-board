package com.single.springboard.web;

import com.single.springboard.service.post.PostService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.post.PostSaveRequest;
import com.single.springboard.web.dto.post.PostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostApiController {
    private final PostService postService;

    @PostMapping
    public Long savePost(
            @ModelAttribute @Valid PostSaveRequest requestDto,
            @LoginUser SessionUser user) {
        return postService.savePostAndFiles(requestDto, user);
    }

    @PatchMapping("/{id}")
    public Long updatePost(@PathVariable Long id, @ModelAttribute @Valid PostUpdateRequest updateDto) {
        return postService.updatePost(id, updateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deletePost(@PathVariable Long id) {
        boolean result = postService.deletePostWithFiles(id);

        return result ? ResponseEntity.ok(id) : ResponseEntity.badRequest().build();
    }
}

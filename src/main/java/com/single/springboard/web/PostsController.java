package com.single.springboard.web;

import com.single.springboard.service.posts.PostsService;
import com.single.springboard.web.dto.PostResponse;
import com.single.springboard.web.dto.PostSaveRequest;
import com.single.springboard.web.dto.PostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostsController {

    private final PostsService postsService;

    @PostMapping("/save")
    public Long savePost(@RequestBody @Valid PostSaveRequest requestDto) {
        return postsService.savePost(requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> findPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postsService.findPostById(id));
    }

    @PutMapping("/{id}")
    public Long updatePost(@PathVariable Long id, @RequestBody @Valid PostUpdateRequest updateDto) {
        return postsService.updatePost(id, updateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boolean result = postsService.deletePost(id);

        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}

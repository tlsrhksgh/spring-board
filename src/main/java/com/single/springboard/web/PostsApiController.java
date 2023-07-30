package com.single.springboard.web;

import com.single.springboard.service.posts.PostsService;
import com.single.springboard.web.dto.posts.PostSaveRequest;
import com.single.springboard.web.dto.posts.PostUpdateRequest;
import com.single.springboard.web.dto.posts.PostsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @PostMapping
    public Long savePost(@RequestBody @Valid PostSaveRequest requestDto) {

        return postsService.savePost(requestDto);
    }

    @GetMapping
    public ResponseEntity<List<PostsResponse>> findAllPosts() {
        return ResponseEntity.ok(postsService.findAllDesc());
    }

    @PutMapping("/{id}")
    public Long updatePost(@PathVariable Long id, @RequestBody @Valid PostUpdateRequest updateDto) {
        return postsService.updatePost(id, updateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deletePost(@PathVariable Long id) {
        boolean result = postsService.deletePost(id);

        return result ? ResponseEntity.ok(id) : ResponseEntity.badRequest().build();
    }
}

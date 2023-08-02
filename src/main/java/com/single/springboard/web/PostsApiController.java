package com.single.springboard.web;

import com.single.springboard.service.posts.PostsService;
import com.single.springboard.web.dto.posts.PostSaveRequest;
import com.single.springboard.web.dto.posts.PostUpdateRequest;
import com.single.springboard.web.dto.posts.PostsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<PostsResponse>> findAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postsService.findAllDesc(pageable));
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

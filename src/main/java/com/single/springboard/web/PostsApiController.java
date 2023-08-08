package com.single.springboard.web;

import com.single.springboard.config.auth.LoginUser;
import com.single.springboard.config.auth.dto.SessionUser;
import com.single.springboard.service.posts.PostsService;
import com.single.springboard.web.dto.posts.PostSaveRequest;
import com.single.springboard.web.dto.posts.PostUpdateRequest;
import com.single.springboard.web.dto.posts.PostsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PostMapping
    public Long savePost(
            @ModelAttribute @Valid PostSaveRequest requestDto,
            @LoginUser SessionUser user) {
        return postsService.savePostAndFiles(requestDto, user.email());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<PostsResponse>> findAllPosts(Pageable pageable) {

        Page<PostsResponse> posts = postsService.findAllPostsAndCommentsCountDesc(pageable);
        return ResponseEntity.ok(posts);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public Long updatePost(@PathVariable Long id, @RequestBody @Valid PostUpdateRequest updateDto) {
        return postsService.updatePost(id, updateDto);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deletePost(@PathVariable Long id) {
        boolean result = postsService.deletePost(id);

        return result ? ResponseEntity.ok(id) : ResponseEntity.badRequest().build();
    }
}

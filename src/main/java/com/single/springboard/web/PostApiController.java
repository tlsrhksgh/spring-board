package com.single.springboard.web;

import com.single.springboard.client.RedisClient;
import com.single.springboard.domain.dto.post.PostListPaginationNoOffset;
import com.single.springboard.service.dto.post.PostSaveRequest;
import com.single.springboard.service.dto.post.PostUpdateRequest;
import com.single.springboard.service.post.PostService;
import com.single.springboard.service.post.dto.CountResponse;
import com.single.springboard.service.post.dto.PostRankingResponse;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostApiController {
    private final PostService postService;
    private final RedisClient redisClient;

    @PostMapping
    public ResponseEntity<Void> savePost(
            @ModelAttribute @Valid PostSaveRequest requestDto,
            @LoginUser SessionUser user) {
        postService.savePostAndFiles(requestDto, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long id,
            @ModelAttribute @Valid PostUpdateRequest updateDto,
            @LoginUser SessionUser user) {
        postService.updatePost(id, updateDto, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @LoginUser SessionUser user) {
        postService.deletePost(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllPost(@RequestBody List<Long> postIds, @LoginUser SessionUser user) {
        postService.deleteAllPost(postIds, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<CountResponse> CountPostAndComment(@LoginUser SessionUser user) {
        return ResponseEntity.ok(postService.countPostAndComment(user));
    }

    @GetMapping("/post-list")
    public ResponseEntity<List<PostListPaginationNoOffset>> findPostList(
            @LoginUser SessionUser user,
            @RequestParam(value = "postId", required = false) Long postId) {
        return ResponseEntity.ok(postService.findWrittenPostByUsername(user, postId));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PostRankingResponse>> postRanking() {
        return ResponseEntity.ok(redisClient.getPostsRanking());
    }
}

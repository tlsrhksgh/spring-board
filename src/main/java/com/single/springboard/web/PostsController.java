package com.single.springboard.web;

import com.single.springboard.service.posts.PostsService;
import com.single.springboard.web.dto.PostSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostsController {

    private final PostsService postsService;

    @PostMapping("/save")
    public Long savePost(@RequestBody PostSaveRequest requestDto) {
        return postsService.savePost(requestDto);
    }
}

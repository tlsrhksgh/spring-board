package com.single.springboard.service.posts;

import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.web.dto.PostSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;

    public Long savePost(PostSaveRequest requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }
}

package com.single.springboard.service.search;

import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PostsRepository postsRepository;

    public List<Posts> findAllPostsByKeyword(String keyword) {
        return postsRepository.findAll(keyword);
    }
}

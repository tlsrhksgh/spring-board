package com.single.springboard.service.search;

import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.web.dto.post.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<SearchResponse> findAllPostsByKeyword(String keyword, Pageable pageable) {
        Page<SearchResponse> postsPage = postRepository.findAllByKeyword(keyword, pageable);

        return new PageImpl<>(postsPage.getContent(), pageable, postsPage.getTotalElements());
    }
}

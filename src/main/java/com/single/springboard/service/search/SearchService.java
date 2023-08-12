package com.single.springboard.service.search;

import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.util.DateUtils;
import com.single.springboard.web.dto.posts.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PostsRepository postsRepository;

    @Transactional(readOnly = true)
    public List<SearchResponse> findAllPostsByKeyword(String keyword) {
        return postsRepository.findAllByKeyword(keyword).stream()
                .map(post -> SearchResponse.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .modifiedDate(DateUtils.formatDate(post.getModifiedDate()))
                        .author(post.getUser().getName())
                        .build())
                .toList();
    }
}

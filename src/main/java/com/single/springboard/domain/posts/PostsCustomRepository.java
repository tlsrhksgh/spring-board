package com.single.springboard.domain.posts;

import com.single.springboard.web.dto.posts.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostsCustomRepository {

    Page<SearchResponse> findAllByKeyword(String keyword, Pageable pageable);

}

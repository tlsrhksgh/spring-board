package com.single.springboard.domain.post;

import com.single.springboard.web.dto.post.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {

    Page<SearchResponse> findAllByKeyword(String keyword, Pageable pageable);

}

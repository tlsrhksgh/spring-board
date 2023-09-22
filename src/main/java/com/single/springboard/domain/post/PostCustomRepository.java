package com.single.springboard.domain.post;

import com.single.springboard.domain.post.dto.PostPaginationDto;
import com.single.springboard.web.dto.post.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostCustomRepository {

    Page<SearchResponse> findAllByKeyword(String keyword, Pageable pageable);

    List<PostPaginationDto> postListPagination(Long postId, String username, int pageSize);
}

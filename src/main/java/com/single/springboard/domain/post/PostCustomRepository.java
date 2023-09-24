package com.single.springboard.domain.post;

import com.single.springboard.domain.post.dto.PostListPaginationDto;
import com.single.springboard.web.dto.post.PostsResponse;
import com.single.springboard.web.dto.post.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostCustomRepository {

    List<PostsResponse> findAllPostWithCommentsNoOffset(Long postId, int pageSize, boolean isLessThen);

    Page<SearchResponse> findAllByKeyword(String keyword, Pageable pageable);

    List<PostListPaginationDto> postListPaginationNoOffset(Long postId, String username, int pageSize);
}

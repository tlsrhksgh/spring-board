package com.single.springboard.domain.post;

import com.single.springboard.domain.post.dto.MainPostListNoOffset;
import com.single.springboard.domain.post.dto.PostListPaginationNoOffset;
import com.single.springboard.web.dto.post.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostCustomRepository {

    List<MainPostListNoOffset> findAllPostWithCommentsNoOffset(Long postId, int pageSize);

    Page<SearchResponse> findAllByKeyword(String keyword, Pageable pageable);

    List<PostListPaginationNoOffset> postListPaginationNoOffset(Long postId, String username, int pageSize);

    void deleteAllPostByIds(List<Long> postIds);
}

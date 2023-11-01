package com.single.springboard.domain.post;

import com.single.springboard.domain.dto.post.MainPostList;
import com.single.springboard.domain.dto.post.PostListPaginationNoOffset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCustomRepository {

    Page<MainPostList> findAllPosts(Pageable pageable);

    List<PostListPaginationNoOffset> postListPaginationNoOffset(Long postId, String username, int pageSize);

    void deleteAllPostByIds(List<Long> postIds, String username);

    void increaseViewCount(@Param("postId") Long postId);
}

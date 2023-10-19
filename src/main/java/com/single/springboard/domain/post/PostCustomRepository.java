package com.single.springboard.domain.post;

import com.single.springboard.domain.post.dto.MainPostList;
import com.single.springboard.domain.post.dto.PostListPaginationNoOffset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostCustomRepository {

    Page<MainPostList> findAllPostWithCommentsCount(Pageable pageable);

    List<PostListPaginationNoOffset> postListPaginationNoOffset(Long postId, String username, int pageSize);

    void deleteAllPostByIds(List<Long> postIds, String username);
}

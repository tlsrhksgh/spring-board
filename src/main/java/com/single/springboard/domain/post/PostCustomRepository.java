package com.single.springboard.domain.post;

import com.single.springboard.domain.post.dto.MainPostListNoOffset;
import com.single.springboard.domain.post.dto.PostListPaginationNoOffset;
import com.single.springboard.web.dto.post.SearchResponse;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface PostCustomRepository {

    List<MainPostListNoOffset> findAllPostWithCommentsNoOffset(Long postId, int pageSize);

    List<PostListPaginationNoOffset> postListPaginationNoOffset(Long postId, String username, int pageSize);

    @Modifying
    void deleteAllPostByIds(List<Long> postIds, String username);
}

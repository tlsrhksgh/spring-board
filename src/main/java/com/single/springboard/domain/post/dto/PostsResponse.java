package com.single.springboard.domain.post.dto;

import com.single.springboard.domain.post.dao.PostsInfoNoOffsetDao;
import lombok.Getter;

import java.util.List;

@Getter
public class PostsResponse {
    private List<PostsInfoNoOffsetDao> postResponses;

    private MainPostPaginationDto pagination;

    public PostsResponse(List<PostsInfoNoOffsetDao> postResponses, MainPostPaginationDto paginationDto) {
        this.postResponses = postResponses;
        this.pagination = paginationDto;
    }
}

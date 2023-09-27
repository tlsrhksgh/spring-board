package com.single.springboard.domain.post.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostsResponse {
    private List<MainPostListNoOffset> postResponses;

    private MainPostPagination pagination;

    public PostsResponse(List<MainPostListNoOffset> postResponses, MainPostPagination paginationDto) {
        this.postResponses = postResponses;
        this.pagination = paginationDto;
    }
}

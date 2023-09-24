package com.single.springboard.domain.post.dto;

import com.single.springboard.web.dto.post.PostsResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class RealPaginationDt {
    private List<PostsResponse> postResponses;

    private PostPaginationDto pagination;

    public RealPaginationDt(List<PostsResponse> postResponses, PostPaginationDto paginationDto) {
        this.postResponses = postResponses;
        this.pagination = paginationDto;
    }
}

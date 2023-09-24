package com.single.springboard.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostPaginationDto {
    private int currentPage;
    private long totalPage;
    private Long firstPostId;
    private Long lastPostId;
    private int size;
    private boolean first;
    private boolean last;

    public int getPreviousPage() {
        return currentPage > 1 ? currentPage - 1 : 1;
    }

    public int getNextPage() {
        return currentPage < totalPage ? currentPage + 1 : 1;
    }

    public boolean isFirst() {
        return currentPage == 1;
    }

    public boolean isLast() {
        return currentPage == totalPage;
    }

    public int getSize() {
        return size;
    }
}

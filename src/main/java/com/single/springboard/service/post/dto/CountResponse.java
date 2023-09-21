package com.single.springboard.service.post.dto;


public record CountResponse(
        Long postCount,
        Long commentCount
) {}

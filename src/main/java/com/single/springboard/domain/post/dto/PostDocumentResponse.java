package com.single.springboard.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Document(indexName = "posts")
public class PostDocumentResponse {
    private String id;

    private String title;

    private String author;

    private String content;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private LocalDateTime modifiedDate;
}

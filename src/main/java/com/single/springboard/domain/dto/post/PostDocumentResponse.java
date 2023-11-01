package com.single.springboard.domain.dto.post;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Document(indexName = "posts")
public class PostDocumentResponse {
    private String id;

    private String title;

    private String author;

    private String content;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private LocalDateTime modifiedDate;
}

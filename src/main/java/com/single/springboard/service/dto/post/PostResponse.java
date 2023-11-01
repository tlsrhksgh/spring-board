package com.single.springboard.service.dto.post;

import com.single.springboard.domain.file.File;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String author;
    private String content;
    private List<File> files;
}

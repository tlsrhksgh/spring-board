package com.single.springboard.service.dto.post;

import com.single.springboard.service.dto.comment.CommentsResponse;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PostDetailResponse {
   private Long id;
   private String author;
   private String content;
   private String title;
   private List<CommentsResponse> comments;
   private List<String> fileNames;
}

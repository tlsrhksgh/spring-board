package com.single.springboard.web;

import com.single.springboard.domain.post.dto.PostDocumentResponse;
import com.single.springboard.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchApiController {
    private final SearchService searchService;

    @GetMapping("/posts/search")
    public ResponseEntity<List<PostDocumentResponse>> search(
            @RequestParam("query") String keyword) {
        List<PostDocumentResponse> searchResponses = searchService.findPostsByKeyword(keyword);
        return ResponseEntity.ok(searchResponses);
    }
}

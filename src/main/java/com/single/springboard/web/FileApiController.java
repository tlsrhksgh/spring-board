package com.single.springboard.web;

import com.single.springboard.service.file.FileService;
import com.single.springboard.service.file.dto.FilesNameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@RestController
public class FileApiController {
    private final FileService fileService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<FilesNameDto>> getPostFiles(@PathVariable Long postId) {
        return ResponseEntity.ok(fileService.getFilesOfPost(postId));
    }
}

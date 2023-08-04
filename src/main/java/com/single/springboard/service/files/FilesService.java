package com.single.springboard.service.files;

import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.files.FilesRepository;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.web.dto.files.FileSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.single.springboard.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FilesService {
    private final FilesRepository filesRepository;
    private final PostsRepository postsRepository;
    private final FilesUtils filesUtils;

    @Transactional
    public void translateFileAndSave(Long postId, List<MultipartFile> filesList) {
        List<FileSaveRequest> files = filesUtils.uploadFiles(filesList);
        saveFiles(postId, files);
    }

    @Transactional
    public void saveFiles(final Long postId, final List<FileSaveRequest> files) {
        if(files.isEmpty()) return;

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        List<Files> filesEntities = new ArrayList<>();

        for(FileSaveRequest file : files) {
            filesEntities.add(file.toEntity(post));
        }

        filesRepository.saveAll(filesEntities);
    }
}

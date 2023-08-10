package com.single.springboard.service.files;

import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.files.FilesRepository;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.util.FilesUtils;
import com.single.springboard.web.dto.files.FileSaveRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;

@Service
@RequiredArgsConstructor
public class FilesService {
    private final FilesRepository filesRepository;
    private final PostsRepository postsRepository;
    private final FilesUtils filesUtils;
    private final EntityManager em;

    public void translateFileAndSave(Long postId, List<MultipartFile> multipartFiles) {
        List<FileSaveRequest> files = filesUtils.uploadFiles(multipartFiles);
        saveFiles(postId, files);
    }

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

    public void deleteChildFiles(Long postId) {
        em.createQuery("delete Files f where f.posts.id = :postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }
}

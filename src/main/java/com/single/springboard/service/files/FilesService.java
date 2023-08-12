package com.single.springboard.service.files;

import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.files.FilesRepository;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.util.FilesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;

@Service
@RequiredArgsConstructor
public class FilesService {
    private final FilesRepository filesRepository;
    private final PostsRepository postsRepository;
    private final FilesUtils filesUtils;
    private final AwsS3Upload s3Upload;

    @Transactional
    public void translateFileAndSave(Long postId, List<MultipartFile> multipartFiles) {
        List<MultipartFile> files = filesUtils.fileMimeTypeCheck(multipartFiles);

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        List<Files> filesEntities = s3Upload.uploadFile(files, post);

        filesRepository.saveAll(filesEntities);
    }

    @Transactional
    public void deleteChildFiles(Posts post) {
        s3Upload.delete(post.getFiles());
        filesRepository.deleteFiles(post.getId());
    }
}

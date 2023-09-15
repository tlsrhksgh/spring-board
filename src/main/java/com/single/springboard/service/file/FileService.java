package com.single.springboard.service.file;

import com.single.springboard.domain.file.File;
import com.single.springboard.domain.file.FileRepository;
import com.single.springboard.domain.post.Post;
import com.single.springboard.service.file.dto.FilesNameDto;
import com.single.springboard.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final FileUtils fileUtils;
    private final AwsS3Upload s3Upload;

    @Transactional
    public List<File> postFilesSave(Post post, List<MultipartFile> multipartFiles) {
        fileUtils.fileMimeTypeCheck(multipartFiles);

        List<File> fileEntities = s3Upload.uploadFile(multipartFiles);
        for(File file : fileEntities) {
            file.setPost(post);
        }

        return fileRepository.saveAll(fileEntities);
    }

    @Transactional
    public List<File> postFilesUpdate(List<File> oldFiles, List<MultipartFile> newFiles, Map<Long, String> oldFileNames) {
        fileUtils.fileMimeTypeCheck(newFiles);

        List<Long> deleteFileIds = findDeleteFiles(oldFiles, oldFileNames);

        if(!deleteFileIds.isEmpty()) {
            s3Upload.delete(fileRepository.findFilesByIds(deleteFileIds));
            fileRepository.deleteFilesByIds(deleteFileIds);
        }

        List<File> newFileEntities = s3Upload.uploadFile(newFiles);
        newFileEntities.forEach(file ->
                file.setPost(oldFiles.get(0).getPost())
        );
        fileRepository.saveAll(newFileEntities);

        return newFileEntities;
    }

    @Transactional
    public String userImageUpdate(String oldImageUrl, List<MultipartFile> newFile) {
        fileUtils.fileMimeTypeCheck(newFile);

        s3Upload.delete(List.of(oldImageUrl));

        return s3Upload.uploadFile(newFile).get(0).getTranslateName();
    }

    public List<FilesNameDto> getFilesOfPost(Long postId) {
        return fileRepository.findAllFileByPost(postId).stream()
                .map(file -> new FilesNameDto(file.getId(), file.getOriginalName()))
                .toList();
    }

    @Transactional
    public void deletePostChildFiles(Post post) {
        List<String> translateFileNames = post.getFiles().stream()
                .map(File::getTranslateName)
                .toList();

        s3Upload.delete(translateFileNames);
        fileRepository.deleteFiles(post.getId());
    }

    public List<Long> findDeleteFiles(List<File> existFiles, Map<Long, String> fileNames) {
        if(existFiles == null || fileNames == null) {
            return new ArrayList<>();
        }

        List<Long> deleteFiles = new ArrayList<>();
        for(File file : existFiles) {
            if(!fileNames.containsValue(file.getOriginalName())) {
                deleteFiles.add(file.getId());
            }
        }

        return deleteFiles;
    }
}

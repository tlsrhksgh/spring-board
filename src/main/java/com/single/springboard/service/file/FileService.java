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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    public List<File> filesUpdate(List<File> oldFiles, List<MultipartFile> newFiles, HashMap<Long, String> oldFileNames) {
        fileUtils.fileMimeTypeCheck(newFiles);

        List<File> toBeDeleteFiles = findDeleteFiles(oldFiles, oldFileNames);

        if(!oldFiles.isEmpty()) {
            s3Upload.delete(toBeDeleteFiles);
            if(oldFiles.size() > 1) {
                fileRepository.deleteFiles(toBeDeleteFiles.get(0).getPost().getId());
            } else {
                fileRepository.delete(toBeDeleteFiles.get(0));
            }
        }

        return s3Upload.uploadFile(newFiles);
    }

    public List<FilesNameDto> getFilesOfPost(Long postId) {
        return fileRepository.findAllFileByPost(postId).stream()
                .map(file -> new FilesNameDto(file.getId(), file.getOriginalName()))
                .toList();
    }

    @Transactional
    public void deletePostChildFiles(Post post) {
        s3Upload.delete(post.getFiles());
        fileRepository.deleteFiles(post.getId());
    }

    public Optional<File> findByTranslateName(String name) {
        return fileRepository.findByTranslateName(name);
    }

    public List<File> findDeleteFiles(List<File> existFiles, HashMap<Long, String> fileNames) {
        List<File> willBeDeleteFiles = new ArrayList<>();
        for(File file : existFiles) {
            if(!fileNames.containsValue(file.getOriginalName())) {
                willBeDeleteFiles.add(file);
            }
        }

        return willBeDeleteFiles;
    }
}

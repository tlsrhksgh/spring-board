package com.single.springboard.service.file;

import com.single.springboard.domain.file.File;
import com.single.springboard.domain.file.FileRepository;
import com.single.springboard.domain.post.Post;
import com.single.springboard.util.FilesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final FilesUtils filesUtils;
    private final AwsS3Upload s3Upload;

    @Transactional
    public List<File> postFilesSave(Post post, List<MultipartFile> multipartFiles) {
        List<MultipartFile> files = filesUtils.fileMimeTypeCheck(multipartFiles);

        List<File> fileEntities = s3Upload.uploadFile(files);
        for(File file : fileEntities) {
            file.setPost(post);
        }

        return fileRepository.saveAll(fileEntities);
    }

    @Transactional
    public String profileImageUpdate(List<MultipartFile> multipartFile) {
        List<MultipartFile> file = filesUtils.fileMimeTypeCheck(multipartFile);

        List<File> fileEntity = s3Upload.uploadFile(file);

        return fileEntity.get(0).getTranslateName();
    }

    @Transactional
    public void deletePostChildFiles(Post post) {
        s3Upload.delete(post.getFiles());
        fileRepository.deleteFiles(post.getId());
    }
}

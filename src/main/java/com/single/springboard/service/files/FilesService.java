package com.single.springboard.service.files;

import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.files.FilesRepository;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.user.User;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.util.FilesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilesService {
    private final FilesRepository filesRepository;
    private final FilesUtils filesUtils;
    private final AwsS3Upload s3Upload;

    @Transactional
    public List<Files> postFilesSave(Posts post, List<MultipartFile> multipartFiles) {
        List<MultipartFile> files = filesUtils.fileMimeTypeCheck(multipartFiles);

        List<Files> filesEntities = s3Upload.uploadFile(files);
        for(Files file : filesEntities) {
            file.setPost(post);
        }

        return filesRepository.saveAll(filesEntities);
    }

    @Transactional
    public String profileImageUpdate(List<MultipartFile> multipartFile) {
        List<MultipartFile> file = filesUtils.fileMimeTypeCheck(multipartFile);

        List<Files> fileEntity = s3Upload.uploadFile(file);

        return fileEntity.get(0).getTranslateName();
    }

    @Transactional
    public void deletePostChildFiles(Posts post) {
        s3Upload.delete(post.getFiles());
        filesRepository.deleteFiles(post.getId());
    }
}

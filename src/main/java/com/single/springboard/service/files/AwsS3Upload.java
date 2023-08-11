package com.single.springboard.service.files;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.util.FilesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AwsS3Upload {
    private final AmazonS3Client s3Client;
    private final FilesUtils filesUtils;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<Files> uploadFile(List<MultipartFile> files, Posts post) {
        List<Files> fileEntities = new ArrayList<>();

        for(MultipartFile file : files) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            String translateFileName = filesUtils.translateSaveFileName(file.getOriginalFilename());

            try {
                s3Client.putObject(new PutObjectRequest(
                        bucket, translateFileName, file.getInputStream(), metadata
                ));
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage());
            }

            fileEntities.add(Files.builder()
                    .translateName(translateFileName)
                    .originalName(file.getOriginalFilename())
                    .createdDate(LocalDateTime.now())
                    .posts(post)
                    .size(file.getSize())
                    .build());
        }

        return fileEntities;
    }


}

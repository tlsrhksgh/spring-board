package com.single.springboard.service.file;

import com.single.springboard.domain.file.File;
import com.single.springboard.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AwsS3Upload {
    private final FileUtils fileUtils;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public List<File> uploadFile(List<MultipartFile> files) {
        List<File> fileEntities = new ArrayList<>();
        PutObjectRequest putObj;

        for (MultipartFile file : files) {
            String keyName = fileUtils.translateFileName(file.getOriginalFilename());

            putObj = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyName)
                    .build();

            try {
                s3Client.putObject(putObj, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            fileEntities.add(File.builder()
                    .translateName(keyName)
                    .originalName(file.getOriginalFilename())
                    .createdDate(LocalDateTime.now())
                    .size(file.getSize())
                    .build());
        }

        return fileEntities;
    }

    @Transactional
    // one request
    public void delete(List<String> fileUrls) {
        ArrayList<ObjectIdentifier> keys = new ArrayList<>();
        for (String fileUrl : fileUrls) {
            keys.add(ObjectIdentifier.builder()
                    .key(fileUrl)
                    .build());
        }

        Delete del = Delete.builder()
                .objects(keys)
                .build();

        DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(del)
                .build();

        s3Client.deleteObjects(multiObjectDeleteRequest);
    }
}

package com.single.springboard.service.files;

import com.single.springboard.web.dto.files.FileSaveRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FilesUtils {
    private final String uploadPath = Paths.get("C:", "develop", "upload-files").toString();

    public List<FileSaveRequest> uploadFiles(final List<MultipartFile> multipartFiles) {
        List<FileSaveRequest> files = new ArrayList<>();
        for(MultipartFile multipartFile : multipartFiles) {
            String mimeType = getMimeType(multipartFile);
            if(!validImageMimeType(mimeType)) {
                throw new RuntimeException("이미지 파일만 업로드 가능합니다.");
            }
            files.add(uploadFile(multipartFile));
        }
        return files;
    }

    private FileSaveRequest uploadFile(final MultipartFile multipartFile)  {
        String saveName = translateSaveFileName(multipartFile.getOriginalFilename());
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String uploadPath = getUploadPath(today) + File.separator + saveName;

        File uploadFile = new File(uploadPath);

        try {
            multipartFile.transferTo(uploadFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return FileSaveRequest.builder()
                .originalName(multipartFile.getOriginalFilename())
                .translateName(saveName)
                .size(multipartFile.getSize())
                .build();
    }

    private String translateSaveFileName(final String fileName) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = StringUtils.getFilenameExtension(fileName);
        return uuid + "." + extension;
    }

    private String getMimeType(MultipartFile multipartFile) {
        Tika tika = new Tika();
        try {
            return tika.detect(multipartFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validImageMimeType(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private String getUploadPath(final String addPath) {
        return makeDirectories(uploadPath + File.separator + addPath);
    }

    private String makeDirectories(final String path) {
        File dir = new File(path);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        return  dir.getPath();
    }
}

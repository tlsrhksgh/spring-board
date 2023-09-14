package com.single.springboard.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileUtils {
    public void fileMimeTypeCheck(final List<MultipartFile> multipartFiles) {
        for(MultipartFile multipartFile : multipartFiles) {
            String mimeType = getMimeType(multipartFile);
            if(!validImageMimeType(mimeType)) {
                throw new RuntimeException("이미지 파일만 업로드 가능합니다.");
            }
        }
    }

    public String translateFileName(final String fileName) {
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
}

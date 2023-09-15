package com.single.springboard.domain.file;

import java.util.List;

public interface FileCustomRepository {

    void deleteFilesByIds(List<Long> ids);

    List<String> findFilesByIds(List<Long> ids);
}

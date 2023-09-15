package com.single.springboard.domain.file.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.file.File;
import com.single.springboard.domain.file.FileCustomRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.single.springboard.domain.file.QFile.file;

@RequiredArgsConstructor
public class FileCustomRepositoryImpl implements FileCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public void deleteFilesByIds(List<Long> ids) {
        query.delete(file)
                .where(file.id.in(ids))
                .execute();
    }

    @Override
    public List<String> findFilesByIds(List<Long> ids) {
        return query.select(file.translateName)
                .from(file)
                .where(file.id.in(ids))
                .fetch();
    }


}

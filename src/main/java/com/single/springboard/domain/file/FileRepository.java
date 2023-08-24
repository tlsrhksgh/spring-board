package com.single.springboard.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    @Modifying(clearAutomatically = true)
    @Query("delete from File f where f.post.id = :postId")
    void deleteFiles(Long postId);

    List<File> findAllByPostId(Long postId);
}

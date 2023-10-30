package com.single.springboard.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long>, FileCustomRepository {
    @Modifying(clearAutomatically = true)
    @Query("delete from File f where f.post.id = :postId")
    void deleteFiles(@Param("postId") Long postId);

    @Query("select f from File f where f.post.id = :postId")
    List<File> findAllFileByPost(@Param("postId") Long postId);
}

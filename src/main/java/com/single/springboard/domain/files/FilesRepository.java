package com.single.springboard.domain.files;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FilesRepository extends JpaRepository<Files, Long> {
    @Modifying(clearAutomatically = true)
    @Query("delete from Files f where f.posts.id = :postId")
    void deleteFiles(Long postId);
}

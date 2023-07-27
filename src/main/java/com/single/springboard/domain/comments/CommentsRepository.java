package com.single.springboard.domain.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    @Query("select c from Comments c where c.posts.id = :postId order by c.id desc, c.createdDate asc ")
    List<Comments> findAllByPostsId(@Param("postId") Long postId);

    @Modifying
    @Query("delete from Comments c where c.id = :commentId")
    void deleteById(@Param("commentId") Long commentId);
}
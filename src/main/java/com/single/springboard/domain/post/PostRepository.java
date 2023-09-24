package com.single.springboard.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    @Query("SELECT p from Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH p.user " +
            "where p.id = :postId")
    Post findPostWithCommentsAndUser(@Param("postId") Long postId);

    @Query("SELECT p from Post p " +
            "LEFT JOIN fetch p.files " +
            "where p.id = :postId")
    Post findPostWithFiles(@Param("postId") Long postId);

    @Query("SELECT COUNT(p.user.name) from Post p where p.user.name = :username")
    Long countPostByUser(@Param("username") String username);
}

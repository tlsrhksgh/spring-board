package com.single.springboard.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    @Query("SELECT p, COUNT(c) FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN FETCH p.user " +
            "GROUP BY p " +
            "ORDER BY p.id DESC")
    Page<Object[]> findAllPostsWithCommentsCountAndUser(Pageable pageable);

    @Query("SELECT p from Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH p.user " +
            "where p.id = :postId")
    Post findPostWithCommentsAndUser(@Param("postId") Long postId);
}

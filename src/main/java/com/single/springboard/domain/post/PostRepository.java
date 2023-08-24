package com.single.springboard.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    @Query("SELECT p, COUNT(c) FROM Post p LEFT JOIN Comment c ON p.id = c.post.id GROUP BY p ORDER BY p.id DESC ")
    Page<Object[]> findAllPostsWithCommentsCount(Pageable pageable);

}

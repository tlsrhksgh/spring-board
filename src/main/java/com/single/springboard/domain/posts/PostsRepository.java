package com.single.springboard.domain.posts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostsRepository extends JpaRepository<Posts, Long>, PostsCustomRepository {
    @Query("SELECT p, COUNT(c) FROM Posts p LEFT JOIN Comments c ON p.id = c.posts.id GROUP BY p ORDER BY p.id DESC ")
    Page<Object[]> findAllPostsWithCommentsCount(Pageable pageable);
}

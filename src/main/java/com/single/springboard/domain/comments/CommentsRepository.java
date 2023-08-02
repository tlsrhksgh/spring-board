package com.single.springboard.domain.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long>, CommentsRepositoryCustom {

    @Query("select c from Comments c where c.posts.id = :postId order by c.parentComment.id asc, c.id desc")
    List<Comments> findAllByComments(@Param("postId") Long postId);

    @Query("select c from Comments c where c.posts.id = :postId and c.id = :parentId")
    Optional<Comments> findByPostIdAndParentId(@Param("postId") Long postId, @Param("parentId") Long parentId);
}

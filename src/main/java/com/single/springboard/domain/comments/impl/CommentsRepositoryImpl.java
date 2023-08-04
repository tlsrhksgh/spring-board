package com.single.springboard.domain.comments.impl;

import com.single.springboard.domain.comments.Comments;
import com.single.springboard.domain.comments.CommentsRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CommentsRepositoryImpl implements CommentsRepositoryCustom {
    private final EntityManager em;

    @Override
    public void delComments(Long parentId) {
        Comments parent = em.find(Comments.class, parentId);
        em.remove(parent);
    }
}

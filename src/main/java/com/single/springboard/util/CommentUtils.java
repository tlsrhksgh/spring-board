package com.single.springboard.util;

import com.single.springboard.domain.comment.Comment;
import com.single.springboard.service.user.dto.SessionUser;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommentUtils {

    public List<Comment> commentsSort(List<Comment> comments) {
        List<Comment> sortedComments = new ArrayList<>();

        Collections.sort(comments, (Comment c1, Comment c2) -> {
            if(c1.getId() < c2.getId()) return 1;
            else return -1;
        });

        for(Comment comment : comments) {
            if(comment.getParentComment() == null) {
                sortedComments.add(comment);
                sortCommentsByLevelRecursive(comment, comments, sortedComments);
            }
        }

        return sortedComments;
    }

    private void sortCommentsByLevelRecursive(Comment parent, List<Comment> comments, List<Comment> sortedComments) {
        List<Comment> children = findChildren(parent.getId(), comments);
        if (!children.isEmpty()) {
            for (Comment child : children) {
                sortedComments.add(child);
                sortCommentsByLevelRecursive(child, comments, sortedComments);
            }
        }
    }

    private List<Comment> findChildren(Long id, List<Comment> comments) {
        List<Comment> children = new ArrayList<>();
        for(Comment comment : comments) {
            if(comment.getParentComment() != null && Objects.equals(comment.getParentComment().getId(), id)) {
                children.add(comment);
            }
        }

        Collections.sort(children, Comparator.comparingLong(Comment::getId));

        return children;
    }

    public boolean enableSecretCommentView(String postAuthor, String currentUser, String commentAuthor) {
        return postAuthor.equals(currentUser) || commentAuthor.equals(currentUser);
    }
}

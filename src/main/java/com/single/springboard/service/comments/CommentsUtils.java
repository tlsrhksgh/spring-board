package com.single.springboard.service.comments;

import com.single.springboard.domain.comments.Comments;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommentsUtils {

    public List<Comments> commentsSort(List<Comments> comments) {
        List<Comments> sortedComments = new ArrayList<>();

        for(Comments comment : comments) {
            if(comment.getParentComment() == null) {
                sortedComments.add(comment);
                sortCommentsByLevelRecursive(comment, comments, sortedComments);
            }
        }

        return sortedComments;
    }

    private void sortCommentsByLevelRecursive(Comments parent, List<Comments> comments, List<Comments> sortedComments) {
        List<Comments> children = findChildren(parent.getId(), comments);
        if (!children.isEmpty()) {
            for (Comments child : children) {
                sortedComments.add(child);
                sortCommentsByLevelRecursive(child, comments, sortedComments);
            }
        }
    }

    private List<Comments> findChildren(Long id, List<Comments> comments) {
        List<Comments> children = new ArrayList<>();
        for(Comments comment : comments) {
            if(comment.getParentComment() != null && Objects.equals(comment.getParentComment().getId(), id)) {
                children.add(comment);
            }
        }

        Collections.sort(children, Comparator.comparingLong(Comments::getId));

        return children;
    }
}

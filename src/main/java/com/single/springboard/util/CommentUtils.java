package com.single.springboard.util;

import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.post.Post;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.comment.CommentsResponse;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class CommentUtils {
    public List<CommentsResponse> createCommentsResponses(List<Comment> comments, Post post, SessionUser user) {
        return commentsSort(comments).stream()
                .map(comment -> CommentsResponse.builder()
                        .id(comment.getId())
                        .commentLevel(comment.getCommentLevel())
                        .parentId(comment.getParentComment())
                        .content(comment.isSecret() ? this.enableSecretCommentView(post.getAuthor(),
                                user.getName(), comment.getAuthor()) ?
                                comment.getContent() : "비밀 댓글 입니다." : comment.getContent())
                        .author(comment.getAuthor())
                        .createdDate(comment.getCreatedDate().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        ))
                        .build())
                .toList();
    }

    private List<Comment> commentsSort(List<Comment> comments) {
        List<Comment> sortedComments = new ArrayList<>();

        comments.sort((Comment c1, Comment c2) -> {
            if (c1.getId() < c2.getId()) return 1;
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

        children.sort(Comparator.comparingLong(Comment::getId));

        return children;
    }

    private boolean enableSecretCommentView(String postAuthor, String currentUser, String commentAuthor) {
        return postAuthor.equals(currentUser) || commentAuthor.equals(currentUser);
    }
}

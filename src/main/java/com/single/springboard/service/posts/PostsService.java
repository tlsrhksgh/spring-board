package com.single.springboard.service.posts;

import com.single.springboard.domain.comments.Comments;
import com.single.springboard.domain.comments.CommentsRepository;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.web.dto.comments.CommentsResponse;
import com.single.springboard.web.dto.posts.PostResponse;
import com.single.springboard.web.dto.posts.PostSaveRequest;
import com.single.springboard.web.dto.posts.PostUpdateRequest;
import com.single.springboard.web.dto.posts.PostsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;

    public Long savePost(PostSaveRequest requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public PostResponse findPostByIdAndComments(Long id) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
        List<Comments> comments = commentsRepository.findAllByComments(post.getId());

        List<Comments> sortedComments = commentsSort(comments);

        List<CommentsResponse> commentsResponses = sortedComments.stream()
                .map(comment -> CommentsResponse.builder()
                        .id(comment.getId())
                        .commentLevel(comment.getCommentLevel())
                        .parentId(comment.getParentId())
                        .content(comment.getContent())
                        .author(comment.getUser().getName())
                        .build())
                .collect(Collectors.toList());

        return PostResponse.builder()
                .author(post.getAuthor())
                .title(post.getTitle())
                .content(post.getContent())
                .comments(commentsResponses)
                .build();
    }

    private List<Comments> commentsSort(List<Comments> comments) {
        List<Comments> sortedComments = new ArrayList<>();

        for(Comments comment : comments) {
            if(comment.getParentId() == 0) {
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
            if(Objects.equals(comment.getParentId(), id)) {
                children.add(comment);
            }
        }

        Collections.sort(children, Comparator.comparingLong(Comments::getId));

        return children;
    }

    @Transactional(readOnly = true)
    public List<PostsResponse> findAllDesc() {
        return postsRepository.findAllPostsDesc().stream()
                .map(post -> new PostsResponse(post.getId(), post.getTitle(),
                        post.getAuthor(), post.getModifiedDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updatePost(Long id, PostUpdateRequest updateDto) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));
        post.updatePost(updateDto);

        return post.getId();
    }

    @Transactional
    public boolean deletePost(Long id) {
        boolean isExistPost = postsRepository.existsById(id);

        if (isExistPost) {
            postsRepository.deleteById(id);
            return true;
        }

        return false;
    }
}

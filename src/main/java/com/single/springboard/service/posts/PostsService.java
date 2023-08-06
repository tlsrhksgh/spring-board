package com.single.springboard.service.posts;

import com.single.springboard.config.auth.LoginUser;
import com.single.springboard.config.auth.dto.SessionUser;
import com.single.springboard.domain.comments.Comments;
import com.single.springboard.domain.comments.CommentsRepository;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.comments.CommentsUtils;
import com.single.springboard.service.files.FilesService;
import com.single.springboard.web.dto.comments.CommentsResponse;
import com.single.springboard.web.dto.posts.PostResponse;
import com.single.springboard.web.dto.posts.PostSaveRequest;
import com.single.springboard.web.dto.posts.PostUpdateRequest;
import com.single.springboard.web.dto.posts.PostsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;
    private final FilesService filesService;
    private final CommentsUtils commentsUtils;
    private final PostsUtils postsUtils;

    @Transactional
    public Long savePostAndFiles(PostSaveRequest requestDto) {
        Long postId = postsRepository.save(requestDto.toEntity()).getId();

        if(requestDto.files() != null) {
            filesService.translateFileAndSave(postId, requestDto.files());
        }

        return postId;
    }

    @Transactional(readOnly = true)
    public PostResponse findPostByIdAndComments(Long id, @LoginUser SessionUser user) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        if(user != null) {
            postsUtils.increasePostViewCount(String.valueOf(id), user.email());
        }

        List<Comments> comments = commentsRepository.findAllByComments(post.getId());
        List<Comments> sortedComments = commentsUtils.commentsSort(comments);

        List<CommentsResponse> commentsResponses = sortedComments.stream()
                .map(comment -> CommentsResponse.builder()
                        .id(comment.getId())
                        .commentLevel(comment.getCommentLevel())
                        .parentId(comment.getParentComment())
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

    @Transactional(readOnly = true)
    public Page<PostsResponse> findAllPostsDesc(Pageable pageable) {
        return postsRepository.findAllPostsDesc(pageable)
                .map(post -> PostsResponse.builder()
                        .id(post.getId())
                        .author(post.getAuthor())
                        .title(post.getTitle())
                        .modifiedDate(post.getModifiedDate())
                        .viewCount(post.getViewCount())
                        .commentsCount(commentsRepository.countCommentsByPostsId(post.getId()))
                        .build());
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
            filesService.deleteChildFiles(id);
            postsRepository.deleteById(id);
            return true;
        }

        return false;
    }
}

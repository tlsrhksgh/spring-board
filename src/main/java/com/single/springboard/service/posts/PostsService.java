package com.single.springboard.service.posts;

import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.web.dto.PostResponse;
import com.single.springboard.web.dto.PostSaveRequest;
import com.single.springboard.web.dto.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.single.springboard.exception.ErrorCode.NOT_EXIST_POST;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;

    public Long savePost(PostSaveRequest requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public PostResponse findPostById(Long id) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_EXIST_POST));

        PostResponse postDto = new PostResponse(post.getId(), post.getAuthor(), post.getContent(), post.getTitle());
        return postDto;
    }

    @Transactional
    public Long updatePost(Long id, PostUpdateRequest updateDto) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_EXIST_POST));
        post.updatePost(updateDto);

        return post.getId();
    }
    
    public boolean deletePost(Long id) {
        boolean isExistPost = postsRepository.existsById(id);

        if(isExistPost) {
            postsRepository.deleteById(id);
            return true;
        }

        return false;
    }
}

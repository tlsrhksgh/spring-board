package com.single.springboard.service.posts;

import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.web.dto.PostSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostsServiceTest {

    @Mock
    private PostsRepository postsRepository;

    @InjectMocks
    private PostsService postsService;

    @Test
    @DisplayName("게시글 저장 성공")
    void savePost_successSavePost() {
        //given
        PostSaveRequest postDto = new PostSaveRequest("test", "hello", "testuser");
        Posts post = Posts.builder()
                .id(1L)
                .content("hello")
                .title("test")
                .author("testuser")
                .build();
        given(postsRepository.save(any()))
                .willReturn(post);

        //when
        Long postId = postsService.savePostAndFiles(postDto);

        //then
        verify(postsRepository, times(1)).save(any(Posts.class));
        assertThat(postId).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글 제목 값이 없어 저장 실패")
    void savePost_titleEmpty_SavePost_Failed() {
        //given
        PostSaveRequest postDto = new PostSaveRequest("test", "hello", "testuser");
        Posts post = Posts.builder()
                .id(1L)
                .content("hello")
                .title("test")
                .author("testuser")
                .build();
        given(postsRepository.save(any()))
                .willReturn(post);

        //when
        Long postId = postsService.savePostAndFiles(postDto);

        //then
        verify(postsRepository, times(1)).save(any(Posts.class));
        assertThat(postId).isEqualTo(1L);
    }

}
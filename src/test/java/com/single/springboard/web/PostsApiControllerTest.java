package com.single.springboard.web;

import com.single.springboard.config.auth.dto.SessionUser;
import com.single.springboard.domain.user.Role;
import com.single.springboard.domain.user.User;
import com.single.springboard.service.posts.PostsService;
import com.single.springboard.web.dto.posts.PostSaveRequest;
import com.single.springboard.web.dto.posts.PostUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostsApiController.class)
class PostsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostsService postsService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void savePost_authorize_success() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",         // 파일 파라미터 이름
                "image.png", // 파일 이름
                MediaType.IMAGE_PNG_VALUE,
                "Hello world!".getBytes()
        );
        User user = User.builder()
                .email("testuser@naver.com")
                .name("user")
                .id(1L)
                .picture(null)
                .role(Role.USER)
                .build();

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", new SessionUser(user));

        PostSaveRequest requestDto = new PostSaveRequest("title", "content", "author", null);
        given(postsService.savePostAndFiles(requestDto, user.getEmail()))
                .willReturn(1L);

        // when
        // then
        mockMvc.perform(multipart("/api/v1/posts")
                        .file(file)
                        .param("title", requestDto.title())
                        .param("author", requestDto.author())
                        .param("content", requestDto.content())
                        .contentType("multipart/form-data")
                        .session(mockHttpSession)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(postsService, times(1)).savePostAndFiles(requestDto, user.getEmail());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void savePost_postTitleIsBlank_failed() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",         // 파일 파라미터 이름
                "image.png", // 파일 이름
                MediaType.IMAGE_PNG_VALUE,
                "Hello world!".getBytes()
        );
        User user = User.builder()
                .email("testuser@naver.com")
                .name("user")
                .id(1L)
                .picture(null)
                .role(Role.USER)
                .build();

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", new SessionUser(user));

        PostSaveRequest requestDto = new PostSaveRequest("", "content", "author", null);

        // when
        // then
        mockMvc.perform(multipart("/api/v1/posts")
                        .file(file)
                        .param("title", requestDto.title())
                        .param("author", requestDto.author())
                        .param("content", requestDto.content())
                        .contentType("multipart/form-data")
                        .session(mockHttpSession)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("제목은 띄어쓰기만 있을 수 없습니다. 한 글자 이상 입력해 주세요."))
                .andExpect(jsonPath("$.errorCode").value(400));

        verify(postsService, times(0)).savePostAndFiles(requestDto, user.getEmail());
    }

    @Test
    @WithAnonymousUser
    void savePost_unauthorized_failed() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",         // 파일 파라미터 이름
                "image.png", // 파일 이름
                MediaType.IMAGE_PNG_VALUE,
                "Hello world!".getBytes()
        );

        PostSaveRequest requestDto = new PostSaveRequest("title", "content", "author", null);

        // when
        // then
        mockMvc.perform(multipart("/api/v1/posts")
                        .file(file)
                        .param("title", requestDto.title())
                        .param("author", requestDto.author())
                        .param("content", requestDto.content())
                        .contentType("multipart/form-data")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(postsService, times(0)).savePostAndFiles(requestDto, null);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updatePost_authorize_success() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateRequest updateDto = new PostUpdateRequest("title", "author", "content", null);

        User user = User.builder()
                .email("testuser@naver.com")
                .name("user")
                .id(1L)
                .picture(null)
                .role(Role.USER)
                .build();

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", new SessionUser(user));
        given(postsService.updatePost(postId, updateDto))
                .willReturn(1L);

        // when
        // then
        mockMvc.perform(patch("/api/v1/posts/{id}", postId)
                        .param("title", updateDto.title())
                        .param("author", updateDto.author())
                        .param("content", updateDto.content())
                        .session(mockHttpSession)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(postsService, times(1)).updatePost(1L, updateDto);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deletePost_authorize_success() throws Exception {
        // given
        Long postId = 1L;

        User user = User.builder()
                .email("testuser@naver.com")
                .name("user")
                .id(1L)
                .picture(null)
                .role(Role.USER)
                .build();

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", new SessionUser(user));
        given(postsService.deletePostWithFiles(postId))
                .willReturn(true);

        // when
        // then
        mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                        .session(mockHttpSession)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(postsService, times(1)).deletePostWithFiles(1L);
    }
}
package com.single.springboard.web;

import com.single.springboard.domain.user.Role;
import com.single.springboard.domain.user.User;
import com.single.springboard.service.post.PostService;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.post.PostSaveRequest;
import com.single.springboard.web.dto.post.PostUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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

@WebMvcTest(value = PostApiController.class)
class PostApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void savePost_authorizedUser_success() throws Exception {
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
        given(postService.savePostAndFiles(requestDto, (SessionUser) mockHttpSession.getAttribute("user")))
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

        verify(postService, times(1)).savePostAndFiles(requestDto,
                (SessionUser) mockHttpSession.getAttribute("user"));
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

        PostSaveRequest requestDto = new PostSaveRequest("", "content", "author", null);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", new SessionUser(user));

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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("제목은 띄어쓰기만 있을 수 없습니다. 한 글자 이상 입력해 주세요."))
                .andExpect(jsonPath("$.errorCode").value(400));

        verify(postService, times(0)).savePostAndFiles(requestDto,
                (SessionUser) mockHttpSession.getAttribute("user"));
    }

    @Test
    @WithAnonymousUser
    void savePost_unAuthorizedUser_failed() throws Exception {
        // given
        PostSaveRequest requestDto = new PostSaveRequest("hello", "content", "guest", null);

        // when
        // then
        mockMvc.perform(multipart("/api/v1/posts")
                        .param("title",requestDto.title())
                        .param("author", requestDto.author())
                        .param("content", requestDto.content())
                        .contentType("application/json")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(postService, times(0)).savePostAndFiles(requestDto, null);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updatePost_authorizeUser_success() throws Exception {
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
        given(postService.updatePost(postId, updateDto))
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

        verify(postService, times(1)).updatePost(1L, updateDto);
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
        given(postService.deletePostWithFiles(postId))
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

        verify(postService, times(1)).deletePostWithFiles(1L);
    }
}
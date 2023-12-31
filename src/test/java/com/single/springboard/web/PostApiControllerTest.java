package com.single.springboard.web;

import com.single.springboard.client.RedisClient;
import com.single.springboard.domain.user.Role;
import com.single.springboard.domain.user.User;
import com.single.springboard.service.dto.post.PostSaveRequest;
import com.single.springboard.service.dto.post.PostUpdateRequest;
import com.single.springboard.service.post.PostService;
import com.single.springboard.service.user.dto.SessionUser;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.session.Session;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
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

    @MockBean
    private RedisClient redisClient;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void savePost_authorizedUser_success() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",         // 파일 파라미터 이름
                "image.png", // 파일 이름
                MediaType.IMAGE_PNG_VALUE,
                "Helloworld!".getBytes()
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
                .andExpect(status().isOk());

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
                .andExpect(status().is3xxRedirection());

        verify(postService, times(0)).savePostAndFiles(requestDto, null);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updatePost_authorizeUser_success() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateRequest updateRequest = new PostUpdateRequest(
                "post", "user", "hello", null, null
        );

        User user = User.builder()
                .email("testuser@naver.com")
                .name("user")
                .id(1L)
                .picture(null)
                .role(Role.USER)
                .build();

        SessionUser sessionUser = new SessionUser(user);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", sessionUser);
        willDoNothing().given(postService)
                        .updatePost(postId, updateRequest, sessionUser);

        // when
        // then
        mockMvc.perform(patch("/api/v1/posts/{id}", postId)
                        .param("title", updateRequest.title())
                        .param("author", updateRequest.author())
                        .param("content", updateRequest.content())
                        .session(mockHttpSession)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(postService, times(1)).updatePost(postId, updateRequest, sessionUser);
    }

    @Test
    @WithMockUser(username = "guestUser", authorities = "ROLE_GUEST")
    void updatePost_unAuthorizedUser_failed() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateRequest updateRequest = new PostUpdateRequest(
                "post", "user", "hello", new ArrayList<>(), null
        );

        // when
        // then
        mockMvc.perform(patch("/api/v1/posts/{id}", postId)
                        .param("title", updateRequest.title())
                        .param("author", updateRequest.author())
                        .param("content", updateRequest.content())
                        .with(csrf())
                        .with(anonymous())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(postService, times(0)).updatePost(1L, updateRequest, null);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deletePost_authorizeUser_success() throws Exception {
        // given
        Long postId = 1L;

        User user = User.builder()
                .email("testuser@naver.com")
                .name("user")
                .id(1L)
                .picture(null)
                .role(Role.USER)
                .build();

        SessionUser sessionUser = new SessionUser(user);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", sessionUser);
        willDoNothing().given(postService)
                        .deletePost(postId, sessionUser);

        // when
        // then
        mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                        .session(mockHttpSession)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(postService, times(1)).deletePost(1L, sessionUser);
    }

    @Test
    @WithMockUser(username = "guestUser", authorities = "ROLE_GUEST")
    void deletePost_unAuthorizedUser_failed() throws Exception {
        // given
        Long postId = 1L;

        // when
        // then
        mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                        .with(csrf())
                        .with(anonymous())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(postService, times(0)).deletePost(1L, null);
    }
}
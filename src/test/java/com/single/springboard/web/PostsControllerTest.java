package com.single.springboard.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.service.posts.PostsService;
import com.single.springboard.web.dto.PostResponse;
import com.single.springboard.web.dto.PostSaveRequest;
import com.single.springboard.web.dto.PostUpdateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostsController.class)
class PostsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostsService postsService;

    @Test
    void savePost_successSavePost() throws Exception {
        //given
        Posts post = Posts.builder()
                .id(1L)
                .author("hello")
                .content("testcontent")
                .title("hello")
                .build();
        PostSaveRequest postDto = new PostSaveRequest("hello", "testcontent", "hello");
        given(postsService.savePost(postDto))
                .willReturn(post.getId());

        //when
        //then
        mockMvc.perform(post("/api/v1/posts/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print());
    }

    @Test
    void savePost_titleBlank_saveFailed() throws Exception {
        //given
        PostSaveRequest postDto = new PostSaveRequest("", "testcontent", "hello");
        given(postsService.savePost(postDto))
                .willReturn(null);

        //when
        //then
        mockMvc.perform(post("/api/v1/posts/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void updatePost_success() throws Exception {
        //given
        Long postId = 1L;
        PostUpdateRequest updateDto = new PostUpdateRequest("updateTitle", "updateContent");

        given(postsService.updatePost(postId, updateDto))
                .willReturn(postId);

        //when
        //then
        mockMvc.perform(put("/api/v1/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePost_postTitleBlank_failed() throws Exception {
        //given
        Long postId = 1L;
        PostUpdateRequest updateDto = new PostUpdateRequest("", "updateContent");

        //when
        //then
        mockMvc.perform(put("/api/v1/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("제목은 1 ~ 500자 이여야 합니다."));
    }

    @Test
    void findPostById_success() throws Exception {
        //given
        PostResponse postResponse = new PostResponse(1L, "testUser", "testContent", "testTitle");
        given(postsService.findPostById(postResponse.id()))
                .willReturn(postResponse);

        //when
        //then
        mockMvc.perform(get("/api/v1/posts/" + postResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.author").value("testUser"))
                .andExpect(jsonPath("$.title").value("testTitle"))
                .andExpect(jsonPath("$.content").value("testContent"))
                .andDo(print());
    }


    @Test
    void deletePost_success() throws Exception {
        //given
        Long postId = 1L;
        given(postsService.deletePost(postId))
                .willReturn(true);

        //when
        //then
        mockMvc.perform(delete("/api/v1/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postId)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePost_NonExistPost_failed() throws Exception {
        //given
        Long postId = 1L;
        given(postsService.deletePost(postId))
                .willReturn(false);

        //when
        //then
        mockMvc.perform(delete("/api/v1/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postId)))
                .andExpect(status().isBadRequest());
    }

}
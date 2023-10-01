package com.single.springboard.web;

import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.domain.post.dto.MainPostListNoOffset;
import com.single.springboard.domain.post.dto.MainPostPagination;
import com.single.springboard.domain.post.dto.PostsResponse;
import com.single.springboard.domain.user.Role;
import com.single.springboard.domain.user.User;
import com.single.springboard.service.post.PostService;
import com.single.springboard.service.search.SearchService;
import com.single.springboard.service.user.dto.SessionUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.session.DefaultMockitoSessionBuilder;
import org.mockito.session.MockitoSessionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private SearchService searchService;

    @Test
    @WithMockUser(username = "guestUser", roles = "GUEST")
    void index_unAuthorizedUser_mainPage_loadSuccess() throws Exception {
        //given
        List<MainPostListNoOffset> postListNoOffsets = Arrays.asList(
                MainPostListNoOffset
                        .builder()
                        .author("testuser1")
                        .commentCount(0L)
                        .title("test")
                        .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .viewCount(1L)
                        .id(1L)
                        .build(),
                MainPostListNoOffset
                        .builder()
                        .author("testuser2")
                        .commentCount(0L)
                        .title("test")
                        .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .viewCount(1L)
                        .id(2L)
                        .build());

        MainPostPagination mainPostPagination = MainPostPagination
                .builder()
                .currentPage(1)
                .first(true)
                .totalPage(20)
                .size(20)
                .last(false)
                .build();

        PostsResponse postsResponse = new PostsResponse(postListNoOffsets, mainPostPagination);
        given(postService.findAllPostAndCommentsCountDesc(1, 20))
                .willReturn(postsResponse);

        //when
        //then
        mockMvc.perform(get("/")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts", "ranking"))
                .andExpect(model().attributeDoesNotExist( "user"));

        verify(postService, times(1)).findAllPostAndCommentsCountDesc(1, 20);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void index_authorizedUser_mainPage_loadSuccess() throws Exception {
        //given
        List<MainPostListNoOffset> postListNoOffsets = Arrays.asList(
                MainPostListNoOffset
                        .builder()
                        .author("testuser1")
                        .commentCount(0L)
                        .title("test")
                        .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .viewCount(1L)
                        .id(1L)
                        .build(),
                MainPostListNoOffset
                        .builder()
                        .author("testuser2")
                        .commentCount(0L)
                        .title("test")
                        .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .viewCount(1L)
                        .id(2L)
                        .build());

        MainPostPagination mainPostPagination = MainPostPagination
                .builder()
                .currentPage(1)
                .first(true)
                .totalPage(20)
                .size(20)
                .last(false)
                .build();

        PostsResponse postsResponse = new PostsResponse(postListNoOffsets, mainPostPagination);
        given(postService.findAllPostAndCommentsCountDesc(1, 20))
                .willReturn(postsResponse);

        //when
        //then
        mockMvc.perform(get("/")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts", "ranking"))
                .andExpect(model().attributeDoesNotExist( "user"));

        verify(postService, times(1)).findAllPostAndCommentsCountDesc(1, 20);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void postSave_authorizedUser_loadSuccess() throws Exception {
        // given
        User user = User.builder()
                .email("test@naver.com")
                .role(Role.USER)
                .name("testuser")
                .id(1L)
                .sameName(false)
                .picture(null)
                .build();
        SessionUser sessionUser = new SessionUser(user);

        // when
        // then
        mockMvc.perform(get("/posts/save")
                        .flashAttr("user", sessionUser))
                .andExpect(status().isOk())
                .andExpect(view().name("post-save"))
                .andExpect(model().attribute("user", sessionUser));
    }
}
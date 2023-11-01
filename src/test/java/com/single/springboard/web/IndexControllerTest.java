package com.single.springboard.web;

import com.single.springboard.domain.dto.post.MainPostList;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.user.User;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.dto.post.PostDetailResponse;
import com.single.springboard.service.dto.post.PostResponse;
import com.single.springboard.service.post.PostService;
import com.single.springboard.service.search.SearchService;
import com.single.springboard.service.user.dto.SessionUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.single.springboard.domain.user.Role.USER;
import static com.single.springboard.exception.ErrorCode.IS_WRONG_ACCESS;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        Pageable pageable =  PageRequest.of(0, 20);
        Page<MainPostList> posts = createDummyPage();
        given(postService.findAllPostAndCommentsCountDesc(pageable))
                .willReturn(posts);

        //when
        //then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeDoesNotExist( "user", "ranking"));

        verify(postService, times(1)).findAllPostAndCommentsCountDesc(pageable);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void index_authorizedUser_mainPage_loadSuccess() throws Exception {
        //given
        Pageable pageable =  PageRequest.of(0, 20);
        Page<MainPostList> posts = createDummyPage();

        User user = User.builder()
                .id(1L)
                .role(USER)
                .name("testuser")
                .email("testuser@naver.com")
                .picture(null)
                .sameName(false)
                .build();

        SessionUser sessionUser = new SessionUser(user);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("user", sessionUser);

        given(postService.findAllPostAndCommentsCountDesc(pageable))
                .willReturn(posts);

        //when
        //then
        mockMvc.perform(get("/")
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("user","posts"))
                .andExpect(model().attributeDoesNotExist(  "ranking"));

        verify(postService, times(1)).findAllPostAndCommentsCountDesc(pageable);
    }

    private Page<MainPostList> createDummyPage() {
        List<MainPostList> postList = new ArrayList<>();
        return new PageImpl<>(postList);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void postSave_authorizedUser_loadSuccess() throws Exception {
        // given
        User user = User.builder()
                .picture(null)
                .sameName(false)
                .role(USER)
                .id(1L)
                .name("testuser")
                .email("test@naver.com")
                .build();
        SessionUser sessionUser = new SessionUser(user);

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("user", sessionUser);

        // when
        // then
        mockMvc.perform(get("/posts/save")
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("testuser")))
                .andExpect(view().name("post-save"));
    }

    @Test
    void postSave_unAuthorizedUser_loadFailed() throws Exception {
        // given
        MockHttpSession mockSession = new MockHttpSession();

        // when
        // then
        mockMvc.perform(get("/posts/save")
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void postUpdate_authorizedUser_loadSuccess() throws Exception {
        // given
        User user = User.builder()
                .picture(null)
                .sameName(false)
                .role(USER)
                .id(1L)
                .name("testuser")
                .email("test@naver.com")
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .user(user)
                .content("반갑습니다!!2")
                .title("안녕하세요2")
                .build();

        PostResponse postResponse = PostResponse.builder()
                .author(user.getName())
                .content(post2.getContent())
                .id(post2.getId())
                .title(post2.getTitle())
                .build();

        SessionUser sessionUser = new SessionUser(user);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("user", sessionUser);

        given(postService.findPostById(2L, sessionUser))
                .willReturn(postResponse);

        // when
        // then
        mockMvc.perform(get("/posts/update/{id}", post2.getId())
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("post", "user"))
                .andExpect(content().string(containsString("testuser")))
                .andExpect(content().string(containsString("반갑습니다!!2")))
                .andExpect(content().string(containsString("안녕하세요2")))
                .andExpect(view().name("post-update"));

        verify(postService, times(1)).findPostById(post2.getId(), sessionUser);
    }

    @Test
    void postUpdate_unAuthorizedUser_loadFailed() throws Exception {
        // given
        User user = User.builder()
                .email("test@naver.com")
                .name("testuser")
                .id(1L)
                .picture(null)
                .sameName(false)
                .build();

        Post post = Post.builder()
                .title("안녕하세요")
                .content("반갑습니다!!")
                .user(user)
                .files(null)
                .id(1L)
                .build();
        // when
        // then
        mockMvc.perform(get("/posts/update/{id}", post.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(postService, never()).findPostById(post.getId(), null);
    }

    @Test
    @WithMockUser(username = "testuser2", roles = "USER")
    void postUpdate_accessAnotherUser_loadFailed() throws Exception {
        // given
        User user = User.builder()
                .picture(null)
                .sameName(false)
                .role(USER)
                .id(1L)
                .name("testuser")
                .email("test@naver.com")
                .build();

        User user2 = User.builder()
                .picture(null)
                .sameName(false)
                .role(USER)
                .id(2L)
                .name("testuser2")
                .email("test2@naver.com")
                .build();

        Post post = Post.builder()
                .id(1L)
                .user(user)
                .content("반갑습니다!!")
                .title("안녕하세요")
                .build();

        SessionUser sessionUser = new SessionUser(user2);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("user", sessionUser);

        given(postService.findPostById(1L, sessionUser))
                .willThrow(new CustomException(IS_WRONG_ACCESS));

        // when
        // then
        mockMvc.perform(get("/posts/update/{id}", post.getId())
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 접근입니다."))
                .andExpect(jsonPath("$.errorCode").value(400));

        verify(postService, times(1)).findPostById(post.getId(), sessionUser);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void postFind_authorizedUser_readMyPost_loadSuccess() throws Exception {
        //given
        User user = User.builder()
                .email("test@naver.com")
                .name("testuser")
                .id(1L)
                .picture(null)
                .sameName(false)
                .build();

        Post post = Post.builder()
                .title("안녕하세요")
                .content("반갑습니다!!")
                .user(user)
                .files(null)
                .id(1L)
                .build();

        SessionUser sessionUser = new SessionUser(user);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("user", sessionUser);

        PostDetailResponse postResponse = PostDetailResponse
                .builder()
                .author(sessionUser.getName())
                .comments(null)
                .content(post.getContent())
                .fileNames(null)
                .id(post.getId())
                .title(post.getTitle())
                .build();

        given(postService.findPostDetail(post.getId(), sessionUser))
                .willReturn(postResponse);

        //when
        //then
        mockMvc.perform(get("/posts/find/" + post.getId())
                        .session(mockSession)
                        .flashAttr("post", postResponse))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("testuser")))
                .andExpect(content().string(containsString("안녕하세요")))
                .andExpect(content().string(containsString("반갑습니다!!")))
                .andExpect(view().name("post-detail"))
                .andExpect(model().attributeExists("user", "post"));

        verify(postService, times(1)).findPostDetail(post.getId(), sessionUser);
    }

    @Test
    @WithMockUser(username = "anonymoususer", roles = "GUEST")
    void postFind_unAuthorizedUser_readPost_loadSuccess() throws Exception {
        //given
        User user = User.builder()
                .email("test@naver.com")
                .name("testuser")
                .id(1L)
                .picture(null)
                .sameName(false)
                .build();

        Post post = Post.builder()
                .title("안녕하세요")
                .content("반갑습니다!!")
                .user(user)
                .files(null)
                .id(1L)
                .build();

        PostDetailResponse postResponse = PostDetailResponse
                .builder()
                .author(post.getUser().getName())
                .comments(null)
                .content(post.getContent())
                .fileNames(null)
                .id(post.getId())
                .title(post.getTitle())
                .build();

        given(postService.findPostDetail(post.getId(), null))
                .willReturn(postResponse);

        //when
        //then
        mockMvc.perform(get("/posts/find/{id}", post.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("testuser")))
                .andExpect(content().string(containsString("안녕하세요")))
                .andExpect(content().string(containsString("반갑습니다!!")))
                .andExpect(view().name("post-detail"))
                .andExpect(model().attributeExists("post"));

        verify(postService, times(1)).findPostDetail(post.getId(), null);
    }

    @Test
    void search_authorizedUser_loadSuccess() {

    }
}
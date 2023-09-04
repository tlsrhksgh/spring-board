package com.single.springboard.web;

import com.single.springboard.domain.user.Role;
import com.single.springboard.domain.user.User;
import com.single.springboard.scheduler.RankingScheduler;
import com.single.springboard.service.post.PostService;
import com.single.springboard.service.post.dto.PostRankResponse;
import com.single.springboard.service.search.SearchService;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.post.PostsResponse;
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

import java.util.Collections;
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

    @MockBean
    private RankingScheduler rankingScheduler;

    @Test
    @WithMockUser(username = "guestUser", roles = "GUEST")
    void index_unAuthorizedUser_loadSuccess() throws Exception {
        //given
        List<PostsResponse> postsResponses = Collections.emptyList();
        Pageable pageable = PageRequest.of(0, 20);
        Page<PostsResponse> page = new PageImpl<>(postsResponses, pageable, 0);
        List<PostRankResponse> postRankResponses = Collections.emptyList();

        given(postService.findAllPostsAndCommentsCountDesc(pageable))
                .willReturn(page);
        given(rankingScheduler.getPostsRanking())
                .willReturn(postRankResponses);

        //when then
        mockMvc.perform(get("/")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts", "ranking"))
                .andExpect(model().attributeDoesNotExist( "user"));

        verify(postService, times(1)).findAllPostsAndCommentsCountDesc(pageable);
        verify(rankingScheduler, times(1)).getPostsRanking();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void index_authorizedUser_loadSuccess() throws Exception {
        //given
        List<PostsResponse> postsResponses = Collections.emptyList();
        Pageable pageable = PageRequest.of(0, 20);
        Page<PostsResponse> page = new PageImpl<>(postsResponses, pageable, 0);
        List<PostRankResponse> postRankResponses = Collections.emptyList();

        User user = User.builder()
                .email("test@naver.com")
                .role(Role.USER)
                .id(1L)
                .picture(null)
                .name("testUser")
                .build();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new SessionUser(user));

        given(postService.findAllPostsAndCommentsCountDesc(pageable))
                .willReturn(page);
        given(rankingScheduler.getPostsRanking())
                .willReturn(postRankResponses);

        //when then
        mockMvc.perform(get("/")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts", "ranking", "user"));

        verify(postService, times(1)).findAllPostsAndCommentsCountDesc(pageable);
        verify(rankingScheduler, times(1)).getPostsRanking();
    }
}
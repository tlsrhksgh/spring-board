package com.single.springboard.service.post;

import com.single.springboard.client.RedisClient;
import com.single.springboard.domain.file.File;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.domain.user.Role;
import com.single.springboard.domain.user.User;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.util.CommentUtils;
import com.single.springboard.util.PostUtils;
import com.single.springboard.service.dto.comment.CommentsResponse;
import com.single.springboard.service.dto.post.PostDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private RedisClient redisClient;

    @Mock
    private CommentUtils commentUtils;

    @Mock
    private PostUtils postUtils;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 조회 성공")
    void postDetail_readSuccess() {
        //given
        User user = User.builder()
                .email("test@naver.com")
                .picture(null)
                .name("testuser")
                .id(2L)
                .role(Role.USER)
                .build();

        Post post = Post.builder()
                .id(2L)
                .viewCount(0)
                .title("hello")
                .content("test")
                .author("testuser")
                .comments(null)
                .files(new ArrayList<>())
                .user(user)
                .build();

        PostDetailResponse postDetail = PostDetailResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .author(post.getAuthor())
                .title(post.getTitle())
                .comments(null)
                .fileNames(post.getFiles().stream()
                        .map(File::getTranslateName)
                        .toList())
                .build();

        SessionUser currentUser = new SessionUser(user);

        given(redisClient.get(post.getId(), PostDetailResponse.class))
                .willReturn(null);
        given(postRepository.findPostDetail(post.getId()))
                .willReturn(post);
        willDoNothing().given(redisClient)
                .put(eq(2L), any());

        //when
        PostDetailResponse response = postService.findPostDetail(post.getId(), currentUser);

        //then
        verify(redisClient, times(1)).get(post.getId(), PostDetailResponse.class);
        verify(redisClient, times(1)).put(eq(post.getId()), any(postDetail.getClass()));
        verify(postRepository, times(1)).findPostDetail(post.getId());
        assertEquals(response.getAuthor(), user.getName());
    }
}
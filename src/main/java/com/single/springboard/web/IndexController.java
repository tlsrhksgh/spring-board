package com.single.springboard.web;

import com.single.springboard.domain.dto.post.PostDocumentResponse;
import com.single.springboard.service.post.PostService;
import com.single.springboard.service.search.SearchService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.service.dto.post.PostDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController {
    private final PostService postService;
    private final SearchService searchService;

    @GetMapping("/")
    public String index(Model model,
                        @LoginUser SessionUser user,
                        Pageable pageable) {
        model.addAttribute("posts", postService.findAllPostAndCommentsCountDesc(pageable));
        model.addAttribute("user", user);

        return "index";
    }

    @GetMapping("/posts/save")
    public String postSave(Model model, @LoginUser SessionUser user) {
        model.addAttribute("user", user);

        return "post-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postUpdate(@PathVariable Long id, Model model, @LoginUser SessionUser user) {
        model.addAttribute("post", postService.findPostById(id, user));
        model.addAttribute("user", user);

        return "post-update";
    }

    @GetMapping("/posts/find/{id}")
    public String postFind(@PathVariable Long id, Model model, @LoginUser SessionUser user) {
        PostDetailResponse post = postService.findPostDetail(id, user);
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("comments", post.getComments());
        model.addAttribute("files", post.getFileNames());

        return "post-detail";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam("query") String keyword,
            @LoginUser SessionUser user,
            Model model) {
        List<PostDocumentResponse> searchResponses = searchService.findPostsByKeyword(keyword);
        model.addAttribute("posts", searchResponses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("user", user);

        return "search-result";
    }

    @GetMapping("/user/info")
    public String userInfo(
            @LoginUser SessionUser user,
            Model model) {
        model.addAttribute("user", user);

        return "user-info";
    }

    @GetMapping("/post-list")
    public String postList(
            @LoginUser SessionUser user,
            Model model) {
        model.addAttribute("user", user);

        return "post-list";
    }

    @GetMapping("/comment-list")
    public String commentList(
            @LoginUser SessionUser user,
            Model model) {
        model.addAttribute("user", user);

        return "comment-list";
    }
}

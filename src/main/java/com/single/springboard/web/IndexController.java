package com.single.springboard.web;

import com.single.springboard.domain.post.dto.PostDocumentResponse;
import com.single.springboard.service.post.PostService;
import com.single.springboard.service.search.SearchService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.post.PostWithElementsResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class IndexController {
    private final PostService postService;
    private final SearchService searchService;

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_GUEST')")
    @GetMapping("/")
    public String index(Model model,
                        @LoginUser SessionUser user,
                        @RequestParam(value = "page", defaultValue = "1") Integer currentPage,
                        @RequestParam(value = "size", defaultValue = "20") Integer pageSize) {
        model.addAttribute("posts", postService.
                findAllPostAndCommentsCountDesc(currentPage, pageSize));
        model.addAttribute("ranking", postService.getPostsRanking());
        model.addAttribute("user", user);

        return "index";
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @GetMapping("/posts/save")
    public String postSave(Model model, @LoginUser SessionUser user) {
        model.addAttribute("user", user);

        return "post-save";
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @GetMapping("/posts/update/{id}")
    public String postUpdate(@PathVariable Long id, Model model, @LoginUser SessionUser user) {
        model.addAttribute("post", postService.findPostById(id, user));
        model.addAttribute("user", user);

        return "post-update";
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_GUEST')")
    @GetMapping("/posts/find/{id}")
    public String postFind(@PathVariable Long id, Model model, @LoginUser SessionUser user) {
        PostWithElementsResponse post = postService.findPostDetail(id, user);
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("comments", post.comments());
        model.addAttribute("files", post.fileName());

        return "post-find";
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_GUEST')")
    @GetMapping("/search")
    public String search(
            @RequestParam("query") String keyword,
            @LoginUser SessionUser user,
            Model model) {
        List<PostDocumentResponse> searchResponses = searchService.findAllPostsByKeyword(keyword);
        model.addAttribute("posts", searchResponses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("user", user);

        return "search-result";
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @GetMapping("/user/info")
    public String userInfo(
            @LoginUser SessionUser user,
            Model model) {
        model.addAttribute("user", user);

        return "user-info";
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @GetMapping("/post-list")
    public String postList(
            @LoginUser SessionUser user,
            Model model) {
        model.addAttribute("user", user);

        return "post-list";
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @GetMapping("/comment-list")
    public String commentList(
            @LoginUser SessionUser user,
            Model model) {
        model.addAttribute("user", user);

        return "comment-list";
    }
}

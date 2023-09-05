package com.single.springboard.web;

import com.single.springboard.service.post.PostService;
import com.single.springboard.service.search.SearchService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.post.PostElementsResponse;
import com.single.springboard.web.dto.post.PostResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@RequiredArgsConstructor
@Controller
public class IndexController {
    private final PostService postService;
    private final SearchService searchService;

    @PreAuthorize("isAnonymous() or hasRole('ROLE_USER')")
    @GetMapping("/")
    public String index(Model model,
                        @LoginUser SessionUser user,
                        Pageable pageable) {
        model.addAttribute("posts", postService.findAllPostsAndCommentsCountDesc(pageable));
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
        PostResponse post = postService.findPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("files", post.files());

        return "post-update";
    }

    @PreAuthorize("isAnonymous() or hasRole('ROLE_USER')")
    @GetMapping("/posts/find/{id}")
    public String postFind(@PathVariable Long id, Model model, @LoginUser SessionUser user) {
        long startTime = System.currentTimeMillis();
        PostElementsResponse post = postService.findPostAndElements(id, user);
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("comments", post.comments());
        model.addAttribute("files", post.fileName());

        long endTime = System.currentTimeMillis();
        System.out.println("처리 시간: " + (endTime - startTime) + "ms");

        return "post-find";
    }

    @PreAuthorize("isAnonymous() or hasRole('ROLE_USER')")
    @GetMapping("/search")
    public String search(
            @RequestParam("query") @NotBlank(message = "검색은 한 글자 이상 입력되어야 합니다.") String keyword,
                         Pageable pageable,
                         Model model) {
        model.addAttribute("posts", searchService.findAllPostsByKeyword(keyword, pageable));
        model.addAttribute("keyword", keyword);

        return "search";
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @GetMapping("/user/info")
    public String userInfo(
            @LoginUser SessionUser user,
            Model model) {
        model.addAttribute("user", user);

        return "user-info";
    }
}

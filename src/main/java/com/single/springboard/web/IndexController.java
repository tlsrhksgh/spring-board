package com.single.springboard.web;

import com.single.springboard.config.auth.LoginUser;
import com.single.springboard.config.auth.dto.SessionUser;
import com.single.springboard.service.posts.PostsService;
import com.single.springboard.web.dto.posts.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController {
    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model,
                        @LoginUser SessionUser user,
                        Pageable pageable) {
        model.addAttribute("posts", postsService.findAllPostsAndCommentsCountDesc(pageable));

        if(user != null) {
            model.addAttribute("userName", user.name());
        }

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

        PostResponse posts = postsService.findPostByIdAndComments(id, user);
        model.addAttribute("posts", posts);

        return "post-update";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts/find/{id}")
    public String postFind(@PathVariable Long id, Model model, @LoginUser SessionUser user) {
        PostResponse post = postsService.findPostByIdAndComments(id, user);
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("comments", post.comments());


        return "post-find";
    }
}

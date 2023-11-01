package com.single.springboard.util;

import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.user.User;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.user.dto.SessionUser;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.single.springboard.exception.ErrorCode.IS_WRONG_ACCESS;

@Component
public class CommonUtils {

    public void authorVerification(Object postOrComment, SessionUser user) {
        User author = User.builder().build();
        if(postOrComment instanceof Post) {
            author = ((Post) postOrComment).getUser();
        } else if(postOrComment instanceof Comment) {
            author = ((Comment) postOrComment).getUser();
        }

        if(!Objects.equals(author.getName(), user.getName()) || !Objects.equals(author.getEmail(), user.getEmail())) {
            throw new CustomException(IS_WRONG_ACCESS);
        }
    }
}

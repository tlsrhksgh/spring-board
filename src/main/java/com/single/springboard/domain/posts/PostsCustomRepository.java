package com.single.springboard.domain.posts;

import java.util.List;

public interface PostsCustomRepository {

    List<Posts> findAllByKeyword(String keyword);

}

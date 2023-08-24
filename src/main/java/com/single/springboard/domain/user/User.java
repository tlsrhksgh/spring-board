package com.single.springboard.domain.user;

import com.single.springboard.domain.BaseTimeEntity;
import com.single.springboard.domain.comments.Comments;
import com.single.springboard.domain.posts.Posts;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USERS")
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Comments> comments;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Posts> posts;

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public User update(String name) {
        this.name = name;
        return this;
    }
}

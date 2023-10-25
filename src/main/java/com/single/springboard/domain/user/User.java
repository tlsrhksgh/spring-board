package com.single.springboard.domain.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.single.springboard.domain.BaseTimeEntity;
import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.post.Post;
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
@Table(name = "USERS", indexes = @Index(name = "idx_user_name", columnList = "name"))
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column
    private String picture;

    @Column
    private boolean sameName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Role role;

    @JsonManagedReference("user-comments")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Comment> comments;

    @JsonManagedReference("post-user")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> posts;

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        this.sameName = false;
        return this;
    }

    public User update(String name) {
        this.name = name;
        this.sameName = false;
        return this;
    }

    public void setSameName() {
        this.sameName = true;
    }
}

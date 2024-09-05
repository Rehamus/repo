package com.repo.domain.comment.entity;


import com.repo.common.Timestamp;
import com.repo.domain.post.entity.Post;
import com.repo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(Long id,String contents, User user, Post post, Comment parent){
        this.id = id;
        this.contents = contents;
        this.user = user;
        this.post = post;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public void updateContent(String contents) {
        this.contents = contents;
    }

    public void deleteChildren(Comment comment) {this.children.remove(comment);}

}

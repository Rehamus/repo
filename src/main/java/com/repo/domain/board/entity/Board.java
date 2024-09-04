package com.repo.domain.board.entity;


import com.repo.common.Timestamp;
import com.repo.domain.board.dto.BoardRequestDto;
import com.repo.domain.post.entity.Post;
import com.repo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Board extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Long postCount;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> post = new ArrayList<>();

    @Builder
    public Board(String title, User user, Long postCount) {
        this.title = title;
        this.user = user;
        this.postCount = postCount;
    }

    public void update(BoardRequestDto requestDto) {
        this.title = requestDto.getTitle();
    }

    public void addPost(Post post) {
        this.post.add(post);
    }
}

package com.repo.domain.post.entity;


import com.repo.common.Timestamp;
import com.repo.domain.board.entity.Board;
import com.repo.domain.comment.entity.Comment;
import com.repo.domain.post.dto.PostRequestDto;
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
public class Post extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 750)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)  // 댓글 관계 추가
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String content, User user, Long viewCount) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.viewCount = viewCount;
    }

    public void update(PostRequestDto requestDto) {
        this.title = (requestDto.getTitle() != null && !requestDto.getTitle().isEmpty()) ? requestDto.getTitle() : this.title;
        this.content = (requestDto.getContent() != null && !requestDto.getContent().isEmpty()) ? requestDto.getContent() : this.content;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }


}

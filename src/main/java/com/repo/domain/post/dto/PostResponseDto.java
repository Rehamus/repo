package com.repo.domain.post.dto;


import com.repo.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostResponseDto {

    private Long id;
    private Long userId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }
}

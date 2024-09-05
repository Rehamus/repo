package com.repo.domain.comment.dto;


import com.repo.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDto {

    private Long id;
    private Long userId;
    private Long postId;
    private String nickname;
    private String contents;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> children = new ArrayList<>();

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.nickname = comment.getUser().getNickname();
        this.postId = comment.getPost().getId();
        this.contents = comment.getContents();
        for (Comment child : comment.getChildren()) {
            this.children.add(new CommentResponseDto(child));
        }
    }

    @Builder
    public CommentResponseDto(Long id, Long userId, Long postId, String nickname, String contents, List<CommentResponseDto> childrenDto, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.postId = postId;
        this.createdAt = createdAt;
        this.contents = contents;
        this.children = childrenDto;
    }
}

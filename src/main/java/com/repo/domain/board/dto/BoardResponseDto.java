package com.repo.domain.board.dto;

import com.repo.domain.board.entity.Board;
import com.repo.domain.post.dto.PostResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class BoardResponseDto {

    private Long id;
    private String title;
    private String nickname;
    private Long postCount;
    private List<PostResponseDto> posts;

    public BoardResponseDto(Board board, boolean post) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.nickname = board.getUser().getNickname();
        this.postCount = board.getPostCount();
        if(post) {
            this.posts = board.getPost().stream()
                    .map(PostResponseDto::new)
                    .toList();
        }
    }
}

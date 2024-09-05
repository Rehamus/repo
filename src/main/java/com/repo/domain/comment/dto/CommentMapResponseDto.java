package com.repo.domain.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class CommentMapResponseDto {

    private int totalPages;
    private List<CommentResponseDto> responseDto;

    public CommentMapResponseDto(int totalPages, List<CommentResponseDto> responseDtoList) {
        this.totalPages = totalPages;
        this.responseDto = responseDtoList;
    }
}

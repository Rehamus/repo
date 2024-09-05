package com.repo.domain.board.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BoardResponseMapDto {

    private int totalPages;
    private List<BoardResponseDto> responseDtoList;

    public BoardResponseMapDto(int totalPages, List<BoardResponseDto> responseDtoList) {
        this.totalPages = totalPages;
        this.responseDtoList = responseDtoList;
    }

}

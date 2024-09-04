package com.repo.domain.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostResponseMapDto {

    private int totalPages;
    private List<PostResponseDto> responseDtoList;

    public PostResponseMapDto(int totalPages, List<PostResponseDto> responseDtoList) {
        this.totalPages = totalPages;
        this.responseDtoList = responseDtoList;
    }
}

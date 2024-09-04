package com.repo.domain.post.dto;

import lombok.Data;

@Data
public class PostSearchCond {

    private Long contentId;
    private Long userId;
    private String keyword;
    private String postType;
    private String sortBy;

}

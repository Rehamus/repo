package com.repo.domain.user.dto;

import com.repo.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {

    private String username;
    private String nickname;
    private AuthorityResponseDto authorities;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = AuthorityResponseDto.builder()
                .authority(user.getAuthorities())
                .status(user.getStatus())
                .build();
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

}

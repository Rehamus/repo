package com.repo.domain.user.dto;

import com.repo.domain.user.entity.User;
import lombok.Data;

@Data
public class UserResponseDto {

    private String username;
    private String nickname;
    private AuthorityResponseDto authorities;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = AuthorityResponseDto.builder()
                .authority(user.getAuthorities())
                .status(user.getStatus())
                .build();
    }

}

package com.repo.domain.user.dto;

import com.repo.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorityResponseDto {

    private User.Authorities authority;
    private User.Status status;

    public AuthorityResponseDto(User.Authorities authority, User.Status status) {
        this.authority = authority;
        this.status = status;
    }

}

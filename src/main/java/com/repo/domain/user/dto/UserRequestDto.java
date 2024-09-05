package com.repo.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDto {

    private String username;
    private String password;
    private String nickname;
    private String adminPassword;

}

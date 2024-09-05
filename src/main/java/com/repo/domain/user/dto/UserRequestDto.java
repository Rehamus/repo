package com.repo.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDto {

    @Size(min = 5, message = "Username must be at least 5 characters long")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,}$", message = "아이디는 5자 이상, 영문과 숫자를 섞어 입력해 주세요")
    private String username;

    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,}$", message = "비밀번호는 5자 이상, 영문과 숫자를 조합해 작성해 주세요")
    private String password;

    private String nickname;
    private String adminPassword;


}

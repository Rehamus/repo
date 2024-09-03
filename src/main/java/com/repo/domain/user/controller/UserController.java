package com.repo.domain.user.controller;

import com.repo.domain.user.dto.TokenResponseDto;
import com.repo.domain.user.dto.UserRequestDto;
import com.repo.domain.user.dto.UserResponseDto;
import com.repo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.signup(userRequestDto));
    }

    @PostMapping("/sign")
    public ResponseEntity<TokenResponseDto> sign(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.sign(userRequestDto));
    }

}

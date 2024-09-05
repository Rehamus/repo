package com.repo.domain.user.controller;

import com.repo.domain.user.dto.TokenRequestDto;
import com.repo.domain.user.dto.TokenResponseDto;
import com.repo.domain.user.dto.UserRequestDto;
import com.repo.domain.user.dto.UserResponseDto;
import com.repo.domain.user.service.UserService;
import com.repo.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인")
    @PostMapping("/sign")
    public ResponseEntity<TokenResponseDto> sign(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.sign(userRequestDto));
    }

    @Operation(summary = "회원 가입")
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.signup(userRequestDto));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/signout")
    public ResponseEntity<String> signout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.signout(userPrincipal.getUser().getId()));
    }

    @Operation(summary = "로그아웃")
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.logout(userPrincipal.getUser().getId()));
    }

    @Operation(summary = "유저 정보 변경")
    @PostMapping("/update")
    public ResponseEntity<UserResponseDto> update(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.update(userPrincipal.getUser().getId(),userRequestDto));
    }

    @Operation(summary = "유저 정보 조회")
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> profile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.profile(userPrincipal.getUser().getId()));
    }

    @Operation(summary = "토큰 재발급")
    @GetMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.refresh(tokenRequestDto));
    }
}

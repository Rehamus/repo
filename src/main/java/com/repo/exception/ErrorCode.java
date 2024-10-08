package com.repo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Basic
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 접근입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 금지 되었습니다."),
    NOTFOUND(HttpStatus.NOT_FOUND, "페이지를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다."),

    //Token
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료 되었습니다."),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "리프레쉬 토큰이 만료 되었습니다."),

    //User
    ALREADY_EXISTING_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디 입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."),
    ALREADY_DELETED(HttpStatus.FORBIDDEN, "이미 탈퇴한 사용자 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ALREADY_BLOCK(HttpStatus.FORBIDDEN, "이미 차단된 사용자 입니다."),

    //Board
    NO_BOARD_PERMISSION(HttpStatus.BAD_REQUEST, "보드를 관리할 권한이 없습니다."),
    BOARD_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 보드입니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "보드가 존재하지 않습니다."),

    //Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."),
    NO_POST_PERMISSION(HttpStatus.NOT_FOUND, "게시물을 관리할 권한이 없습니다."),

    //Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}

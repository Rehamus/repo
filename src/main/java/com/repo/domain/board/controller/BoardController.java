package com.repo.domain.board.controller;

import com.repo.domain.board.dto.BoardRequestDto;
import com.repo.domain.board.dto.BoardResponseDto;
import com.repo.domain.board.dto.BoardResponseMapDto;
import com.repo.domain.board.service.BoardService;
import com.repo.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시판 생성")
    @PostMapping
    public ResponseEntity<BoardResponseDto> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody BoardRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.create(userPrincipal.getUser().getId(), requestDto));
    }

    @Operation(summary = "게시판 수정")
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updatePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestBody BoardRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.update(userPrincipal.getUser().getId(), boardId, requestDto));
    }

    @Operation(summary = "게시판 삭제")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId) {

        boardService.delete(userPrincipal.getUser().getId(), boardId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "게시판 조회")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> readPost(@PathVariable Long boardId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.read(boardId));
    }

    @Operation(summary = "게시판 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<BoardResponseMapDto> readPostsByPostType(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam boolean asc) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.reads(page, pageSize, asc));
    }

    @Operation(summary = "게시판 검색")
    @GetMapping("/search")
    public ResponseEntity<BoardResponseMapDto> readPostsByKeyword(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam boolean asc) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.search(keyword, page, pageSize, asc));
    }
}

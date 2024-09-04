package com.repo.domain.comment.controller;

import com.repo.domain.comment.dto.CommentMapResponseDto;
import com.repo.domain.comment.dto.CommentRequestDto;
import com.repo.domain.comment.dto.CommentResponseDto;
import com.repo.domain.comment.service.CommentService;
import com.repo.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/{boardId}/post/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성")
    @PostMapping
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDto commentRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(postId, userPrincipal, commentRequestDto));
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDto commentRequestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.update(postId, commentId, userPrincipal.getUser().getId(), commentRequestDto));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        commentService.delete(postId, commentId, userPrincipal.getUser().getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "댓글 죄회")
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> read(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId) {

        return ResponseEntity.status(HttpStatus.OK).
                body(commentService.read(boardId, postId, commentId));
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ResponseEntity<CommentMapResponseDto> reads(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        return ResponseEntity.status(HttpStatus.OK).
                body(commentService.reads(postId, page, pageSize));
    }

}

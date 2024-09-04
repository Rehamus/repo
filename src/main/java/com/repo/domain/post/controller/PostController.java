package com.repo.domain.post.controller;

import com.repo.domain.post.dto.PostRequestDto;
import com.repo.domain.post.dto.PostResponseDto;
import com.repo.domain.post.dto.PostResponseMapDto;
import com.repo.domain.post.service.PostService;
import com.repo.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/{boardId}/post")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성")
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestBody PostRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.create(boardId, userPrincipal.getUser().getId(), requestDto));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestBody PostRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.update(boardId, userPrincipal.getUser().getId(), postId, requestDto));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        postService.delete(boardId, userPrincipal.getUser().getId(), postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "게시글 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> readPost(
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.read(boardId, postId));
    }

    @Operation(summary = "게시판의 게시글 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<PostResponseMapDto> readPostsByPostType(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam boolean asc) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.reads(boardId, page, pageSize, asc));
    }

    @Operation(summary = "게시글 검색")
    @GetMapping("/search")
    public ResponseEntity<PostResponseMapDto> readPostsByKeyword(
            @PathVariable Long boardId,
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam boolean asc) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.search(boardId, keyword, page, pageSize, asc));
    }


}

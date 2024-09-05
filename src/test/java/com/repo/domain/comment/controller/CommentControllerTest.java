package com.repo.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repo.domain.comment.dto.CommentMapResponseDto;
import com.repo.domain.comment.dto.CommentRequestDto;
import com.repo.domain.comment.dto.CommentResponseDto;
import com.repo.domain.comment.service.CommentService;
import com.repo.domain.user.entity.User;
import com.repo.security.jwt.JwtService;
import com.repo.security.principal.UserDetailsServiceImpl;
import com.repo.security.principal.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        userPrincipal = new UserPrincipal(
                new User(1L, "testUser", "태스터", "password", User.Authorities.USER, User.Status.NORMAL));

        commentRequestDto = new CommentRequestDto(null, "태스트용 내용");
        commentResponseDto = CommentResponseDto.builder().id(1L).userId(1L).nickname("태스터").contents("태스트용 내용").build();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void createCommentTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(commentService.create(anyLong(), any(UserPrincipal.class), any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        // When
        mockMvc.perform(post("/api/board/1/post/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentRequestDto))
                                .with(csrf()))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.contents").value(commentResponseDto.getContents()));

        verify(commentService, times(1)).create(anyLong(), any(UserPrincipal.class), any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void updateCommentTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(commentService.update(anyLong(), anyLong(), anyLong(), any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        // When
        mockMvc.perform(put("/api/board/1/post/1/comments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentRequestDto))
                                .with(csrf()))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.contents").value(commentResponseDto.getContents()));

        verify(commentService, times(1)).update(anyLong(), anyLong(), anyLong(), any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void deleteCommentTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        doNothing().when(commentService).delete(anyLong(), anyLong(), anyLong());

        // When
        mockMvc.perform(delete("/api/board/1/post/1/comments/1").with(csrf()))
                // Then
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).delete(anyLong(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("댓글 조회 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void readCommentTest() throws Exception {
        // Given
        when(commentService.read(anyLong(), anyLong(), anyLong())).thenReturn(commentResponseDto);

        // When
        mockMvc.perform(get("/api/board/1/post/1/comments/1"))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.contents").value(commentResponseDto.getContents()));

        verify(commentService, times(1)).read(anyLong(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("댓글 목록 조회 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void readCommentsTest() throws Exception {
        // Given
        CommentMapResponseDto responseMapDto = new CommentMapResponseDto();
        when(commentService.reads(anyLong(), anyInt(), anyInt())).thenReturn(responseMapDto);

        // When
        mockMvc.perform(get("/api/board/1/post/1/comments")
                                .param("page", "0")
                                .param("pageSize", "10"))
                // Then
                .andExpect(status().isOk());

        verify(commentService, times(1)).reads(anyLong(), anyInt(), anyInt());
    }
}

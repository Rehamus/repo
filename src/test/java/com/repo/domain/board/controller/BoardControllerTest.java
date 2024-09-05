package com.repo.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repo.domain.board.dto.BoardRequestDto;
import com.repo.domain.board.dto.BoardResponseDto;
import com.repo.domain.board.dto.BoardResponseMapDto;
import com.repo.domain.board.service.BoardService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private UserPrincipal userPrincipal;
    private BoardRequestDto boardRequestDto;
    private BoardResponseDto boardResponseDto;

    @BeforeEach
    void setUp() {
        userPrincipal = new UserPrincipal(
                new User(1L,
                         "testUser",
                         "testUser",
                         "password",
                         User.Authorities.USER,
                         User.Status.NORMAL));
        boardRequestDto = new BoardRequestDto("태스트용 제목");
        boardResponseDto = BoardResponseDto.builder()
                .title("태스트용 제목")
                .nickname("태스터")
                .build();
    }

    @Test
    @DisplayName("게시판 생성 테스트")
    @WithMockUser
    void createPostTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(boardService.create(anyLong(), any(BoardRequestDto.class))).thenReturn(boardResponseDto);

        // When
        mockMvc.perform(post("/api/board")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(boardRequestDto)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(boardResponseDto.getId()))
                .andExpect(jsonPath("$.title").value(boardResponseDto.getTitle()));

        verify(boardService, times(1)).create(anyLong(), any(BoardRequestDto.class));
    }

    @Test
    @DisplayName("게시판 수정 테스트")
    @WithMockUser
    void updatePostTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(boardService.update(anyLong(), anyLong(), any(BoardRequestDto.class))).thenReturn(boardResponseDto);

        // When
        mockMvc.perform(put("/api/board/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(boardRequestDto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(boardResponseDto.getId()))
                .andExpect(jsonPath("$.title").value(boardResponseDto.getTitle()));

        verify(boardService, times(1)).update(anyLong(), anyLong(), any(BoardRequestDto.class));
    }

    @Test
    @DisplayName("게시판 삭제 테스트")
    @WithMockUser
    void deletePostTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        doNothing().when(boardService).delete(anyLong(), anyLong());

        // When
        mockMvc.perform(delete("/api/board/1")
                                .with(csrf()))
                // Then
                .andExpect(status().isNoContent());

        verify(boardService, times(1)).delete(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시판 조회 테스트")
    @WithMockUser
    void readPostTest() throws Exception {
        // Given
        when(boardService.read(anyLong())).thenReturn(boardResponseDto);

        // When
        mockMvc.perform(get("/api/board/1"))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(boardResponseDto.getId()))
                .andExpect(jsonPath("$.title").value(boardResponseDto.getTitle()));

        verify(boardService, times(1)).read(anyLong());
    }

    @Test
    @DisplayName("게시판 목록 조회 테스트")
    @WithMockUser
    void readPostsByPostTypeTest() throws Exception {
        // Given
        BoardResponseMapDto responseMapDto = new BoardResponseMapDto();
        when(boardService.reads(anyInt(), anyInt(), anyBoolean())).thenReturn(responseMapDto);

        // When
        mockMvc.perform(get("/api/board/list")
                                .param("page", "0")
                                .param("pageSize", "20")
                                .param("asc", "true"))
                // Then
                .andExpect(status().isOk());

        verify(boardService, times(1)).reads(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("게시판 검색 테스트")
    @WithMockUser
    void readPostsByKeywordTest() throws Exception {
        // Given
        BoardResponseMapDto responseMapDto = new BoardResponseMapDto();
        when(boardService.search(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(responseMapDto);

        // When
        mockMvc.perform(get("/api/board/search")
                                .param("keyword", "태스트")
                                .param("page", "0")
                                .param("pageSize", "20")
                                .param("asc", "true"))
                // Then
                .andExpect(status().isOk());

        verify(boardService, times(1)).search(anyString(), anyInt(), anyInt(), anyBoolean());
    }
}

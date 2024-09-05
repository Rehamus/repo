package com.repo.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repo.domain.post.dto.PostRequestDto;
import com.repo.domain.post.dto.PostResponseDto;
import com.repo.domain.post.dto.PostResponseMapDto;
import com.repo.domain.post.entity.Post;
import com.repo.domain.post.service.PostService;
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

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;
    private PostRequestDto postRequestDto;
    private PostResponseDto postResponseDto;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "testUser", "태스터", "password", User.Authorities.USER, User.Status.NORMAL);
        userPrincipal = new UserPrincipal(user);

        postRequestDto = new PostRequestDto("Test Title", "Test Content");
        postResponseDto = new PostResponseDto(new Post(1L, "Test Title", "Test Content", user, 0L));
    }

    @Test
    @DisplayName("게시글 생성 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void createPostTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(postService.create(anyLong(), anyLong(), any(PostRequestDto.class))).thenReturn(postResponseDto);

        // When
        mockMvc.perform(post("/api/board/1/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postRequestDto))
                                .with(csrf()))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(postResponseDto.getId()))
                .andExpect(jsonPath("$.title").value(postResponseDto.getTitle()))
                .andExpect(jsonPath("$.content").value(postResponseDto.getContent()));

        verify(postService, times(1)).create(anyLong(), anyLong(), any(PostRequestDto.class));
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void updatePostTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(postService.update(anyLong(), anyLong(), anyLong(), any(PostRequestDto.class))).thenReturn(postResponseDto);

        // When
        mockMvc.perform(put("/api/board/1/post/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postRequestDto))
                                .with(csrf()))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postResponseDto.getId()))
                .andExpect(jsonPath("$.title").value(postResponseDto.getTitle()))
                .andExpect(jsonPath("$.content").value(postResponseDto.getContent()));

        verify(postService, times(1)).update(anyLong(), anyLong(), anyLong(), any(PostRequestDto.class));
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void deletePostTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        doNothing().when(postService).delete(anyLong(), anyLong(), anyLong());

        // When
        mockMvc.perform(delete("/api/board/1/post/1")
                                .with(csrf()))
                // Then
                .andExpect(status().isNoContent());

        verify(postService, times(1)).delete(anyLong(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void readPostTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(postService.read(anyLong(), anyLong())).thenReturn(postResponseDto);

        // When
        mockMvc.perform(get("/api/board/1/post/1"))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postResponseDto.getId()))
                .andExpect(jsonPath("$.title").value(postResponseDto.getTitle()))
                .andExpect(jsonPath("$.content").value(postResponseDto.getContent()));

        verify(postService, times(1)).read(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시판의 게시글 목록 조회 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void readPostsByPostTypeTest() throws Exception {
        // Given
        PostResponseMapDto responseMapDto = new PostResponseMapDto();
        when(postService.reads(anyLong(), anyInt(), anyInt(), anyBoolean())).thenReturn(responseMapDto);

        // When
        mockMvc.perform(get("/api/board/1/post/list")
                                .param("page", "0")
                                .param("pageSize", "20")
                                .param("asc", "true"))
                // Then
                .andExpect(status().isOk());

        verify(postService, times(1)).reads(anyLong(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("게시글 검색 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void readPostsByKeywordTest() throws Exception {
        // Given
        PostResponseMapDto responseMapDto = new PostResponseMapDto();
        when(postService.search(anyLong(), anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(responseMapDto);

        // When
        mockMvc.perform(get("/api/board/1/post/search")
                                .param("keyword", "test")
                                .param("page", "0")
                                .param("pageSize", "20")
                                .param("asc", "true"))
                // Then
                .andExpect(status().isOk());

        verify(postService, times(1)).search(anyLong(), anyString(), anyInt(), anyInt(), anyBoolean());
    }
}

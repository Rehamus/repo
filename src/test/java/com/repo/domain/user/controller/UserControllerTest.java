package com.repo.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repo.domain.user.dto.TokenResponseDto;
import com.repo.domain.user.dto.UserRequestDto;
import com.repo.domain.user.dto.UserResponseDto;
import com.repo.domain.user.entity.User;
import com.repo.domain.user.service.UserService;
import com.repo.security.jwt.JwtService;
import com.repo.security.principal.UserDetailsServiceImpl;
import com.repo.security.principal.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;
    private TokenResponseDto tokenResponseDto;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "testUser", "태스터", "password", User.Authorities.USER, User.Status.NORMAL);
        userPrincipal = new UserPrincipal(user);

        userRequestDto = new UserRequestDto("testUser", "password", "닉네임",null);
        userResponseDto = new UserResponseDto(user);
        tokenResponseDto = new TokenResponseDto("accessToken", "refreshToken");
    }

    @Test
    @DisplayName("로그인 테스트")
    void signTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(userService.sign(any(UserRequestDto.class))).thenReturn(tokenResponseDto);

        // When
        mockMvc.perform(post("/api/sign")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto))
                                .with(csrf()))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(tokenResponseDto.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(tokenResponseDto.getRefreshToken()));

        verify(userService, times(1)).sign(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("회원 가입 테스트")
    void signupTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(userService.signup(any(UserRequestDto.class))).thenReturn(userResponseDto);

        // When
        mockMvc.perform(post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto))
                                .with(csrf()))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(userResponseDto.getNickname()))
                .andExpect(jsonPath("$.username").value(userResponseDto.getUsername()));

        verify(userService, times(1)).signup(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void signoutTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(userService.signout(anyLong())).thenReturn("User signed out successfully.");

        // When
        mockMvc.perform(delete("/api/signout")
                                .with(csrf()))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().string("User signed out successfully."));

        verify(userService, times(1)).signout(anyLong());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void logoutTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(userService.logout(anyLong())).thenReturn("User logged out successfully.");

        // When
        mockMvc.perform(delete("/api/logout")
                                .with(csrf()))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out successfully."));

        verify(userService, times(1)).logout(anyLong());
    }

    @Test
    @DisplayName("유저 정보 변경 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void updateTest() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
        when(userService.update(anyLong(), any(UserRequestDto.class))).thenReturn(userResponseDto);

        // When
        mockMvc.perform(post("/api/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto))
                                .with(csrf()))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userResponseDto.getUsername()))
                .andExpect(jsonPath("$.nickname").value(userResponseDto.getNickname()));

        verify(userService, times(1)).update(anyLong(), any(UserRequestDto.class));
    }
}

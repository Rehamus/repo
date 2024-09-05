package com.repo.domain.user.service;

import com.repo.domain.user.dto.TokenResponseDto;
import com.repo.domain.user.dto.UserRequestDto;
import com.repo.domain.user.dto.UserResponseDto;
import com.repo.domain.user.entity.User;
import com.repo.domain.user.repository.UserRepository;
import com.repo.exception.BusinessException;
import com.repo.exception.ErrorCode;
import com.repo.security.jwt.JwtService;
import com.repo.security.principal.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;
    private TokenResponseDto tokenResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .password("encodedPassword")
                .nickname("Tester")
                .status(User.Status.NORMAL)
                .authorities(User.Authorities.USER)
                .build();

        userRequestDto = new UserRequestDto("testUser", "password", "Tester",null);

        userResponseDto = new UserResponseDto(user);

        tokenResponseDto = new TokenResponseDto("accessToken", "refreshToken");
    }

    @Test
    @DisplayName("회원 가입 테스트")
    void signupTest() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponseDto response = userService.signup(userRequestDto);

        // Then
        assertThat(response.getUsername()).isEqualTo(userRequestDto.getUsername());
        assertThat(response.getNickname()).isEqualTo(userRequestDto.getNickname());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 사용자 이름으로 회원 가입 실패 테스트")
    void signupDuplicateUsernameTest() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> userService.signup(userRequestDto), ErrorCode.ALREADY_EXISTING_USER.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 테스트")
    void signTest() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        // When
        TokenResponseDto response = userService.sign(userRequestDto);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(jwtService, times(1)).generateAccessToken(any(User.class));
        verify(jwtService, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void signoutTest() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        userService.signout(1L);

        // Then
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        userService.logout(1L);

        // Then
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원 정보 업데이트 테스트")
    void updateTest() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        userService.update(1L, userRequestDto);

        // Then
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 예외 발생 테스트")
    void getUserNotFoundTest() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> userService.getUser(1L), ErrorCode.USER_NOT_FOUND.getMessage());
    }
}

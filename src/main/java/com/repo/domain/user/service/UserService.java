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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserResponseDto signup(UserRequestDto userRequestDto) {

        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTING_USER);
        }

        User user = User.builder()
                .username(userRequestDto.getUsername())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .nickname(userRequestDto.getNickname())
                .status(User.Status.NORMAL)
                .authorities(User.Authorities.ADMIN)
                .build();

        userRepository.save(user);

        return new UserResponseDto(user);

    }

    public TokenResponseDto sign(UserRequestDto userRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequestDto.getUsername(),
                        userRequestDto.getPassword(),
                        null
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = getUser(userPrincipal.getUsername());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new TokenResponseDto(accessToken, refreshToken);
    }


    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }
}

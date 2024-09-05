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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${ADMINPASSWORD}")
    private String adminPassword;

    public UserResponseDto signup(UserRequestDto userRequestDto) {

        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTING_USER);
        }

        boolean admin = false;

        if(userRequestDto.getAdminPassword()!=null) {
            admin = userRequestDto.getAdminPassword().equals(adminPassword);
        }


        User user = User.builder()
                .username(userRequestDto.getUsername())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .nickname(userRequestDto.getNickname())
                .status(User.Status.NORMAL)
                .authorities(admin ? User.Authorities.ADMIN : User.Authorities.USER)
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

    @Transactional
    public String signout(Long userId) {
        User user = getUser(userId);

        user.signout();

        return null;
    }

    @Transactional
    public String logout(Long userId) {
        User user = getUser(userId);

        user.logout();

        return null;
    }

    @Transactional
    public UserResponseDto update(Long userId, UserRequestDto userRequestDto) {
        User user = getUser(userId);

        user.update(userRequestDto);

        return null;
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

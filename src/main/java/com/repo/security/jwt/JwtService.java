package com.repo.security.jwt;

import com.repo.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j(topic = "JwtService")
public class JwtService {

    @Value("${jwt.key}")
    private String secretKey;

    @Value("${jwt.access-expire-time}")
    private long accessExpireTime;

    @Value("${jwt.refresh-expire-time}")
    private long refreshExpireTime;

    private Key key;
    public static final String CLAIM_ID = "id";
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_STATUS = "status";
    private static final String BEARER_PREFIX = "Bearer ";

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 엑세스 토큰 생성
    public String generateAccessToken(User user) {
        return BEARER_PREFIX+createToken(user, accessExpireTime);
    }

    // 리프레쉬 토큰 생성
    public String generateRefreshToken(User user) {
        return BEARER_PREFIX+createToken(user, refreshExpireTime);
    }

    // 토큰 생성 로직
    private String createToken(User user, long expirationTime) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .claim(CLAIM_ID, user.getId())
                .claim(CLAIM_AUTHORITIES, user.getAuthorities())
                .claim(CLAIM_STATUS, user.getStatus())
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // 토큰 유효성 검증 메서드 (액세스 및 리프레시 공통)
    public boolean isTokenValidate(String token) {
        log.info("isTokenValidate 메서드 실행. 검사할 토큰 : {}", token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 모든 Claims 추출
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String tokenSubstring(String token) {
        if(!token.startsWith(BEARER_PREFIX)){
            return token.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // 토큰에서 사용자 정보 추출
    public User getUserFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return User.builder()
                .username(claims.getSubject())
                .authorities(User.Authorities.valueOf((String) claims.get(CLAIM_AUTHORITIES)))
                .status(User.Status.valueOf((String) claims.get(CLAIM_STATUS)))
                .build();
    }

    public String getErrorMessage(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return "정상 JWT token 입니다.";
        } catch (JwtException e) {
            return "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.";
        } catch (IllegalArgumentException e) {
            return "JWT claims is empty, 잘못된 JWT 토큰 입니다.";
        }
    }
}

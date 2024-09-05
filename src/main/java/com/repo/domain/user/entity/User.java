package com.repo.domain.user.entity;

import com.repo.domain.user.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String username;

    private String nickname;

    private String password;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Authorities authorities;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder
    public User(Long id, String username, String nickname, String password, Authorities authorities, Status status) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.authorities = authorities;
        this.status = status;
    }

    public boolean isBlocked() {
        return this.status == Status.DELETED ;
    }

    @Transient
    public boolean isActivity() {
        return this.status == Status.NORMAL;
    }

    public void update(UserRequestDto userRequestDto) {
        this.nickname = userRequestDto.getNickname();
    }

    public void signout() {
        this.status = Status.DELETED;
        logout();
    }

    public void logout() {
        this.refreshToken = null;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Authorities {
        USER,
        ADMIN
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        NORMAL,
        DELETED
    }

}

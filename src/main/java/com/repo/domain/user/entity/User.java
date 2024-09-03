package com.repo.domain.user.entity;

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

    @Enumerated(EnumType.STRING)
    private Authorities authorities;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder
    public User(String username,String nickname ,String password, Authorities authorities , Status status) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.authorities = authorities;
        this.status = status;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Authorities{
        USER,
        ADMIN
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status{
        NORMAL,
        DELETED,
        BLOCKED
    }

    public boolean isBlocked(){
        return this.status == Status.DELETED || this.status == Status.BLOCKED;
    }

    @Transient
    public boolean isActivity() {
        return this.status == Status.NORMAL;
    }

    public boolean isAdmin(){
        return authorities.equals(Authorities.ADMIN);
    }

}

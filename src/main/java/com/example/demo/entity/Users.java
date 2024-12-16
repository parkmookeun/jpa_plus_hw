package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String nickname;
    private String password;
    private String status; // NORMAL, BLOCKED

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    public Users(String role, String email, String nickname, String password) {
        this.role = Role.of(role);
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public Users() {}

    public void updateStatusToBlocked() {
        this.status = "BLOCKED";
    }
}
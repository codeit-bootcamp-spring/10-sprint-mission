package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private final String username;
    private final String password;

    public LoginRequest(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자 이름(아이디)은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        this.username = username;
        this.password = password;
    }
}

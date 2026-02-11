package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class UserCreateRequest {
    private final String name;
    private final String email;
    private final String password;
    private final BinaryContentRequest binaryContent;

    public UserCreateRequest(String name, String email, String password, BinaryContentRequest binaryContent) {
        validate(name, email, password);
        this.name = name;
        this.email = email;
        this.password = password;
        this.binaryContent = binaryContent;
    }

    public UserCreateRequest(String name, String email, String password) {
        this(name, email, password, null);
    }

    private void validate(String name, String email, String password) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("이름은 필수입니다.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("이메일은 필수입니다.");
        if (!email.contains("@")) throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("비밀번호는 필수입니다.");
    }
}
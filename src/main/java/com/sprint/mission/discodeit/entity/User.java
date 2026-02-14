package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 베이스 코드를 참고하여, 필드 이름 변경(접두어 제거)
    // userNickname -> username, userEmail -> email
    private String username;
    private String email;
    private String password; // 추가
    private UUID profileId; // 추가 - BinaryContent 참조용

    public User(String username, String email, String password, UUID profileId) {
        validateUser(username, email, password);
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        boolean anyValueUpdated = false;

        if (newUsername != null && !newUsername.equals(this.username)) {
            validateUsername(newUsername);
            this.username = newUsername;
            anyValueUpdated = true;
        }

        if (newEmail != null && !newEmail.equals(this.email)) {
            validateEmail(newEmail);
            this.email = newEmail;
            anyValueUpdated = true;
        }

        if (newPassword != null && !newPassword.equals(this.password)) {
            validatePassword(newPassword);
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) super.update();
    }

    // 유저 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
    // 각 필드 별로 검증 로직 분리
    private void validateUser(String username, String email, String password) {
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank() || username.contains(" "))
            throw new IllegalArgumentException("닉네임은 공백 없이 필수 입력 사항입니다.");
        if (username.length() < 2 || username.length() > 10)
            throw new IllegalArgumentException("닉네임은 2자 이상, 10자 이하로 설정하세요.");
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || email.contains(" "))
            throw new IllegalArgumentException("이메일은 공백 없이 필수 입력 사항입니다.");
        // 필요 시 여기서 정규표현식으로 이메일 형식을 검증할 수 있습니다.
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.contains(" "))
            throw new IllegalArgumentException("비밀번호는 공백 없이 8자 이상이어야 합니다.");
    }

    @Override
    public String toString() {
        return String.format("User[닉네임: %s, 이메일: %s, User ID: %s]", username, email, getId());
    }
}
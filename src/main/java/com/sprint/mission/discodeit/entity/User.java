package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
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
        if (username == null || username.isBlank() || username.contains(" ") ||
                username.length() < 2 || username.length() > 10)
            throw new BusinessException(ErrorCode.INVALID_USERNAME);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || email.contains(" "))
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.contains(" "))
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }

    @Override
    public String toString() {
        return String.format("User[닉네임: %s, 이메일: %s, User ID: %s]", username, email, getId());
    }
}
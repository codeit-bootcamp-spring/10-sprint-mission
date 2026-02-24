package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String email;
    private String password;
    private UUID profileId; // BinaryContent의 id를 참조(프로필 이미지)

    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }


    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        // 기존 프로필 이미지를 수정하거나 없었는데 추가 하려는 경우
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }
        // 기존에 프로필 이미지가 있을 때 프로필 이미지를 없애려는 경우
        if (newProfileId == null && this.profileId != null) {
            this.profileId = null;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}

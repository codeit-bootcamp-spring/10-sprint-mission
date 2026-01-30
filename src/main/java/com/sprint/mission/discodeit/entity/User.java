package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@ToString
public class User implements Serializable {
    @ToString.Exclude
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id = UUID.randomUUID();
    private final Long createdAt = Instant.now().getEpochSecond();
    private Long updatedAt = createdAt;
    //
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @Getter
    private final UUID profileId;
    @Getter
    private final UUID userStatusId;

    public void update(String newUsername, String newEmail, String newPassword) {
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

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }
}

package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String password;
    private String profileId;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateProfileId(String profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

    public void update(String newUsername, String newEmail, String newPassword) {
        boolean updated = false;

        if (newUsername != null && !newUsername.isBlank()) {
            this.username = newUsername;
            updated = true;
        }
        if (newEmail != null && !newEmail.isBlank()) {
            this.email = newEmail;
            updated = true;
        }
        if (newPassword != null && !newPassword.isBlank()) {
            this.password = newPassword;
            updated = true;
        }

        if (updated) {
            this.updatedAt = Instant.now();
        }
    }
}

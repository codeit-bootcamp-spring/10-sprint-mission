package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String email;
    private String password;
    private String nickname;
    private UUID profileId; // BinaryContent

    public User(String email,
                String password,
                String nickname,
                UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileId = profileId;
    }

    public void update(String newPassword, String newNickname) {
        boolean anyValueUpdated = false;
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newNickname != null && !newNickname.equals(this.nickname)) {
            this.nickname = newNickname;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public void changeProfileId(UUID newProfileId) {
        if (!Objects.equals(this.profileId, newProfileId)) {
            this.profileId = newProfileId;
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return String.format(
                "User [id=%s, nickname=%s, email=%s]",
                getId().toString().substring(0, 5),
                nickname,
                email
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

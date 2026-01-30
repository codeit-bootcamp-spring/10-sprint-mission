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

    public User(String email, String password, String nickname, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileId = profileId;
    }

    public void update(String newPassword, String newNickname, UUID newProfileId) {
        this.password = newPassword;
        this.nickname = newNickname;
        this.profileId = newProfileId;
        // 업데이트 시간 갱신
        this.updatedAt = Instant.now();
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

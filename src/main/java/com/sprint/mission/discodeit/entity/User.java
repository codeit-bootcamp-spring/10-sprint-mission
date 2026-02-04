package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant updatedAt;

    private String username;
    private String email;
    private String password;

    private UUID profileImageId;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void update(String newUsername, String newEmail, String newPassword) {
        Optional.ofNullable(newUsername).filter(s -> !s.isBlank()).ifPresent(this::setUsername);
        Optional.ofNullable(newEmail).filter(s -> !s.isBlank()).ifPresent(this::setEmail);
        Optional.ofNullable(newPassword).filter(s -> !s.isBlank()).ifPresent(this::setPassword);
    }
}

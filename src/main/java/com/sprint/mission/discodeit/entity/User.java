package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.util.TimeConverter;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserUpdateDto;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final UUID id;
    private final Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private String username;
    private String email;
    private String password;
    private UUID profileId;
    // todo: add message, channel id list
    // todo: add parameters of update, which is messageId, channelId

    public User(UUID id, String username, String email,
                String password, UUID profileId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }

    public boolean matchUsername(String username) {
        return this.username.equals(username);
    }

    public boolean matchEmail(String email) {
        return this.email.equals(email);
    }

    public void update(UserUpdateDto dto) {
        boolean hasUpdated = false;
        hasUpdated |= updateIfChanged(this.username, dto.username(), val -> this.username = val);
        hasUpdated |= updateIfChanged(this.email, dto.email(), val -> this.email = val);
        hasUpdated |= updateIfChanged(this.password, dto.password(), val -> this.password = val);
        hasUpdated |= updateIfChanged(this.profileId, dto.profileId(), val -> this.profileId = val);
        if (hasUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public UserResponse toResponse(boolean isActive) {
        return UserResponse.builder()
                .id(id)
                .username(username)
                .email(email)
                .online(isActive)
                .profileId(profileId)
                .createdAt(TimeConverter.toDateTime(createdAt))
                .updatedAt(TimeConverter.toDateTime(updatedAt))
                .build();
    }
}

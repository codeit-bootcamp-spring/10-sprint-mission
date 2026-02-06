package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserServiceDTO.UserInfoUpdate;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

@ToString
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final UUID id = UUID.randomUUID();
    private final Long createdAt = Instant.now().getEpochSecond();
    private Long updatedAt = createdAt;
    private String username;
    private String email;
    private String password;
    private UUID profileId;
    // todo: add message, channel id list
    // todo: add parameters of update, which is messageId, channelId

    public User(@NonNull String username, @NonNull String email,
                @NonNull String password, UUID profileId) {
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

    private <T> boolean updateIfChanged(T current, T next, Consumer<T> action) {
        if (current == null || current.equals(next)) {
            return false;
        }
        action.accept(next);
        return true;
    }

    public void update(UserInfoUpdate model) {
        boolean hasUpdated = false;
        hasUpdated |= updateIfChanged(this.username, model.newUsername(), val -> this.username = val);
        hasUpdated |= updateIfChanged(this.email, model.newEmail(), val -> this.email = val);
        hasUpdated |= updateIfChanged(this.password, model.newPassword(), val -> this.password = val);
        hasUpdated |= updateIfChanged(this.profileId, model.newProfileId(), val -> this.profileId = val);
        if (hasUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }

    public UserResponse toResponse(boolean isActive) {
        return UserResponse.builder()
                .userId(id)
                .username(username)
                .email(email)
                .isActive(isActive)
                .profileId(profileId)
                .build();
    }
}

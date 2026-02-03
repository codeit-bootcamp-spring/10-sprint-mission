package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;
@Getter
@AllArgsConstructor
public class UserStatusDto {
    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
    private boolean isOnline;

    public UserStatusDto(UserStatus userStatus) {
        this.id = userStatus.getId();
        this.userId = userStatus.getUserId();
        this.lastActiveAt = userStatus.getLastActiveAt();
        this.isOnline = userStatus.isOnline();
    }
}

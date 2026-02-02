package com.sprint.mission.discodeit.dto.request.userStatus;

import com.sprint.mission.discodeit.entity.UserStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusUpdateRequestDTO {
    @NotNull
    private UUID id;

    @NotNull
    private UserStatusType userStatusType;

    private Instant lastOnlineTime;
}

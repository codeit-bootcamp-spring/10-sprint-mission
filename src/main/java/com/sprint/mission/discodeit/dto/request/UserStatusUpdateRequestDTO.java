package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.UserStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UserStatusUpdateRequestDTO {
    @NotNull
    private UserStatusType userStatusType;

    private Instant lastOnlineTime;
}

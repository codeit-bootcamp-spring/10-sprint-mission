package com.sprint.mission.discodeit.dto.request.userStatus;

import com.sprint.mission.discodeit.entity.UserStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserStatusUpdateRequestDTO (
    @NotNull
    UserStatusType userStatusType,

    Instant lastOnlineTime
) {

}

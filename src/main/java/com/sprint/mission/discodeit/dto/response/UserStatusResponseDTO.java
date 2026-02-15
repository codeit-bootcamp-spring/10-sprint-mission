package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatusType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserStatusResponseDTO(
        UUID id,
        UUID userId,
        UserStatusType userStatusType,
        Instant createdAt,
        Instant updatedAt,
        Instant lastOnlineTime
){

}

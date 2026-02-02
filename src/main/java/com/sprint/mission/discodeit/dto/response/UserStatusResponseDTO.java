package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatusType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public class UserStatusResponseDTO {
    private UUID id;
    private UUID userId;
    private UserStatusType userStatusType;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastOnlineTime;
}

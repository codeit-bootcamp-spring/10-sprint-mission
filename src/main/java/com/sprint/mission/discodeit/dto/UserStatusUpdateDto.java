package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserStatusUpdateDto {
    private final UUID id;
    private final UUID userId;
    private final Instant lastActiveAt;
    private final boolean isOnline;
}

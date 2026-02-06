package com.sprint.mission.discodeit.dto.userStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserStatusResponseDto {
    private UUID id;
    private UUID userId;
    private Instant lastOnlineAt;
}

package com.sprint.mission.discodeit.dto.userstatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserStatusRequest {
    private UUID userId;
    private String status;   // "ONLINE", "OFFLINE", "AWAY"
    private Instant lastActiveAt;
}

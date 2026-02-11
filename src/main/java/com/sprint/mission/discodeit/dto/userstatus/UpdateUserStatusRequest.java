package com.sprint.mission.discodeit.dto.userstatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {
    private UUID id;
    private String status;
    private Instant lastActiveAt;
}

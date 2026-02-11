package com.sprint.mission.discodeit.dto.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(UUID id,
                                    UUID userId,
                                    Instant lastSeenAt,
                                    boolean online) {
}

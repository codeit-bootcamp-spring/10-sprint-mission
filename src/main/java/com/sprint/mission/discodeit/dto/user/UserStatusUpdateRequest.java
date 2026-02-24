package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;

public record UserStatusUpdateRequest(
    Instant newLastActiveAt
) {

}
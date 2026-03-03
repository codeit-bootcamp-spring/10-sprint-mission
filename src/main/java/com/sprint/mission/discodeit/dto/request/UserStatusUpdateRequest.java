package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record UserStatusUpdateRequest(
        Instant newLastActiveAt
) {
}

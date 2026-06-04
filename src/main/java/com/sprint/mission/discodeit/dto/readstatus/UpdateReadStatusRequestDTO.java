package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UpdateReadStatusRequestDTO(
        Instant newLastReadAt,
        Boolean newNotificationEnabled
) { }

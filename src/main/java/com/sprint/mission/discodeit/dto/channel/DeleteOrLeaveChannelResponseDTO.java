package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;

public record DeleteOrLeaveChannelResponseDTO(
        Instant timestamp,
        int status,
        String message
) { }

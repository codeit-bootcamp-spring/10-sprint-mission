package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;

public record DeleteChannelResponseDTO (
        Instant timestamp,
        int status,
        String message
) { }

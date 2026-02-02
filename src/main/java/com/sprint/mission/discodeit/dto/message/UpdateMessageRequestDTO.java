package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record UpdateMessageRequestDTO(
        UUID messageId,
        String content
) { }

package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID requestUserId,
        UUID messageId,
        String content
) {
}

package com.sprint.mission.discodeit.dto.message.input;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageUpdateInput(
        @NotNull(message = "ID가 null입니다.")
        UUID messageId,
        UUID requestUserId,
        String content
) {
}

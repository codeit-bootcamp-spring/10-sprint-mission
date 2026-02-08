package com.sprint.mission.discodeit.dto.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageUpdateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID requestUserId,

        @NotNull(message = "ID가 null입니다.")
        UUID messageId,

        @NotBlank(message = "content가 입력되지 않았습니다.")
        String content
) {
}

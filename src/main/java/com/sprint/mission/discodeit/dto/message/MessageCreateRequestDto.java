package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MessageCreateRequestDto(
        String content,

        @NotBlank
        UUID channelId,

        @NotBlank
        UUID authorId
) {
}

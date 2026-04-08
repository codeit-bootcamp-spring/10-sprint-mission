package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateMessageRequestDTO(
        @NotNull(message = "content는 null일 수 없습니다.")
        @NotBlank(message = "content는 공백이 될 수 없습니다.")
        String content,
        @NotNull(message = "channelId는 null일 수 없습니다.")
        UUID channelId,
        @NotNull(message = "authorId는 null일 수 없습니다.")
        UUID authorId
) { }
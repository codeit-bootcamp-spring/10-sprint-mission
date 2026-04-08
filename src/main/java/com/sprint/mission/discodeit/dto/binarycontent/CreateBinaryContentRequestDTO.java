package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBinaryContentRequestDTO(
        @NotNull(message = "userId는 null일 수 없습니다.")
        UUID userId,
        @Nullable
        UUID messageId,
        @NotNull(message = "data는 null일 수 없습니다.")
        byte[] data,
        @NotNull(message = "contentType은 null일 수 없습니다.")
        String contentType,
        String filename
) { }

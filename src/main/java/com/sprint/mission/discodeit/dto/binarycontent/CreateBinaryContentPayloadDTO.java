package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.validation.constraints.NotNull;

public record CreateBinaryContentPayloadDTO(
        @NotNull(message = "data는 null일 수 없습니다.")
        byte[] data,
        @NotNull(message = "contentType은 null일 수 없습니다.")
        String contentType,
        String filename
) { }

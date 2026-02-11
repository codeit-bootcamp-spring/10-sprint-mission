package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BinaryContentCreateRequest(
        @NotEmpty(message = "binaryContent가 입력되지 않았습니다.")
        byte[] binaryContent
) {
    public BinaryContentCreateRequest {
        if (binaryContent != null) {
            binaryContent = binaryContent.clone();
        }
    }
}

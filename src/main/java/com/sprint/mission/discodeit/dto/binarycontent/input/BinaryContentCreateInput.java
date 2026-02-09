package com.sprint.mission.discodeit.dto.binarycontent.input;

import jakarta.validation.constraints.NotEmpty;

public record BinaryContentCreateInput(
        @NotEmpty(message = "binaryContent가 입력되지 않았습니다.")
        byte[] binaryContent
) {
    public BinaryContentCreateInput {
        if (binaryContent != null) {
            binaryContent = binaryContent.clone();
        }
    }
}

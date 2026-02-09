package com.sprint.mission.discodeit.dto.binarycontent.input;

import com.sprint.mission.discodeit.validation.ValidationMethods;
import jakarta.validation.constraints.NotEmpty;

public record BinaryContentCreateInput(
        @NotEmpty(message = "binaryContent가 입력되지 않았습니다.")
        byte[] bytes,

        String contentType
) {
    public BinaryContentCreateInput {
        if (bytes != null) {
            bytes = bytes.clone();
        }
    }
}

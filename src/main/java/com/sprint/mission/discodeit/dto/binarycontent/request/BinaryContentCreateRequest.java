package com.sprint.mission.discodeit.dto.binarycontent.request;

import jakarta.validation.constraints.NotEmpty;

public record BinaryContentCreateRequest(

        String fileName,
        String contentType,

        @NotEmpty(message = "binaryContent가 입력되지 않았습니다.")
        byte[] bytes
) {
    public BinaryContentCreateRequest {
        if (bytes != null) {
            bytes = bytes.clone();
        }
    }
}

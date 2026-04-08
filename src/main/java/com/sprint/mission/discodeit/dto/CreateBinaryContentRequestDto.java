package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateBinaryContentRequestDto(
        @NotBlank(message = "파일 이름이 존재하지 않습니다.")
        String fileName,

        @NotBlank
        String contentType,

        @Min(value = 1, message = "파일 크기는 0보다 커야합니다.")
        long size,

        @NotEmpty(message = "파일 내용이 비어있습니다.")
        byte[] contents
) {
}

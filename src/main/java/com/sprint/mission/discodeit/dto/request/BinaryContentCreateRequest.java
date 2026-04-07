package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
        @NotBlank(message = "filename은 필수입니다.")
        @Size(max = 50, message = "name은 50자 이하여야 합니다.")
        String fileName,
        @NotBlank(message = "contentType은 필수입니다.")
        String contentType,
        @NotNull(message = "bytes는 필수입니다.")
        @Size(min = 1, message = "파일 데이터가 비어있습니다.")
        byte[] bytes
) {

}

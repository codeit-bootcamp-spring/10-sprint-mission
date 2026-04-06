package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "fileName은 필수입니다.")
    @Size(max = 255, message = "fileName은 255자 이하여야 합니다.")
    String fileName,
    @NotBlank(message = "contentType은 필수입니다.")
    @Size(max = 100, message = "contentType은 100자 이하여야 합니다.")
    String contentType,
    @NotEmpty(message = "bytes는 비어 있을 수 없습니다.")
    byte[] bytes
) {

}

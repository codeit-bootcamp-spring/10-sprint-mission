package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일 이름은 필수 입력 사항입니다.")
    String fileName,

    @NotBlank(message = "컨텐츠 타입은 필수 입력 사항입니다.")
    String contentType,

    @NotNull(message = "파일 데이터는 null일 수 없습니다.")
    @NotEmpty(message = "파일 내용이 비어있습니다.")
    byte[] bytes
) {

}

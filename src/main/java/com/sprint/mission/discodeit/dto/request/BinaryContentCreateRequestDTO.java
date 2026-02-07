package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BinaryContentCreateRequestDTO(
    @NotNull(message = "생성하려는 BinaryContent의 content가 null일 수 없습니다")
    byte[] content,
    @NotBlank(message = "생성하려는 BinaryContent의 contentType이 blank일 수 없습니다")
    String contentType
) {}

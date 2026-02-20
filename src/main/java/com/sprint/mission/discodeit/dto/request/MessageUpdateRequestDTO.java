package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequestDTO(
   @NotBlank(message = "newContent가 blank일 수 없습니다")
   String newContent
) {}

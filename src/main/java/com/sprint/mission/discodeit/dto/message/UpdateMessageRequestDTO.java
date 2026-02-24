package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateMessageRequestDTO(
        @NotNull(message = "content는 null일 수 없습니다.")
        @NotBlank(message = "content는 공백이 될 수 없습니다.")
        String newContent
) { }

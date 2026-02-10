package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
        @NotBlank(message = "메시지를 입력해주세요.")
        String newContent
) {
}

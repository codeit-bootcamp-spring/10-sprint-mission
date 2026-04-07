package com.sprint.mission.discodeit.message.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
    @NotBlank(message = "메시지 내용 은 비어 있을 수 없습니다.")
    String newContent
) {

}

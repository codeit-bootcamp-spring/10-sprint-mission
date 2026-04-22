package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "수정할 Message 내용")
public record MessageUpdateRequest(
    @Size(min = 1)
    @Schema(description = "수정할 Message 내용", example = "메시지 수정")
    String newContent
) {

}

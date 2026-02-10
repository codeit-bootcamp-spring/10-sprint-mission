package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MessageUpdateRequestDTO (
    @NotBlank
    String message
) {

}

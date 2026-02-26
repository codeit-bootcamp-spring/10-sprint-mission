package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;

public record MessagePatchDto(
    @NotBlank
    String newContent
) {

}

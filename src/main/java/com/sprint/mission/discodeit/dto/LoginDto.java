package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(
    @NotBlank
    String username,

    @NotBlank
    String password
) {

}

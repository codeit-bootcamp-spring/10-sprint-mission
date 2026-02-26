package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicChannelPostDto(
    @NotBlank
    String name,

    @NotNull
    String description
) {

}

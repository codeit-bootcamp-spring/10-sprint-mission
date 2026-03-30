package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChannelPatchDto(
    @NotBlank
    String newName,

    @NotNull
    String newDescription
) {

}

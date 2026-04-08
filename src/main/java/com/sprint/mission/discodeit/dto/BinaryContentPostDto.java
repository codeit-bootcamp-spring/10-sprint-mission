package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BinaryContentPostDto(
    @NotNull
    UUID userId,

    @NotNull
    UUID messageId,

    @NotBlank
    String fileName,

    @NotNull
    byte[] data
) {

}

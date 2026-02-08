package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BinaryContentDto {
    public record CreateRquest(
            @NotBlank
            String fileName,
            @NotBlank
            String contentType,
            @NotBlank
            byte[] content
    ) {}
}

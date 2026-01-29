package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class BinaryContentDto {
    public record CreateRequest(
            @NotBlank
            String fileName,

            @NotBlank
            String contentType,

            @NotBlank
            byte[] content
    ) {}

}

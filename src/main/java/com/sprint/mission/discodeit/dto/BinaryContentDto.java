package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class BinaryContentDto {
    public record CreateRequest(
            @NotBlank
            String fileName,

            @NotBlank
            String contentType,

            @NotNull
            byte[] content
    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            String fileName,
            String contentType,
            byte[] content,
            long size
    ) {}

}

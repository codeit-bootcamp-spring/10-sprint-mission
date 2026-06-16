package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class BinaryContentDto {
    @Schema(name = "BinaryContentCreateRequest", description = "BinaryContent 생성 정보")
    public record CreateRequest(
            @NotBlank
            String fileName,

            @NotBlank
            String contentType,

            @NotNull
            byte[] bytes
    ) {}

    @Schema(name = "BinaryContentResponse", description = "BinaryContent 응답 정보")
    public record Response(
            UUID id,
            String fileName,
            long size,
            String contentType,
            BinaryContentStatus status
    ) {}

}

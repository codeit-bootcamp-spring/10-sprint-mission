package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "첨부 파일 정보")
public record BinaryContentResponse(
        @Schema(description = "첨부 파일 ID", example = "a1636f5f-72ab-497f-a6ff-72e4eb6f9f54")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-02-20T11:00:00Z")
        Instant createdAt,
        @Schema(description = "첨부 파일 이름", example = "profile.png")
        String fileName,
        @Schema(description = "첨부 파일 크기", example = "12345")
        Long size,
        @Schema(description = "첨부 파일 content type", example = "image/png")
        String contentType,
        @Schema(description = "첨부 파일 원본 bytes", example = "aGVsbG8=")
        byte[] bytes
) {
    public static BinaryContentResponse from(
            BinaryContent binaryContent
    ) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType(),
                binaryContent.getBytes()
        );
    }
}

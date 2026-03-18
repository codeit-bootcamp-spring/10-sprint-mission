package com.sprint.mission.discodeit.dto.binarycontent;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "첨부 파일 DTO")
public record BinaryContentDto(
        @Schema(description = "첨부 파일 ID", example = "a1636f5f-72ab-497f-a6ff-72e4eb6f9f54")
        UUID id,
        @Schema(description = "파일 이름", example = "profile.png")
        String fileName,
        @Schema(description = "파일 크기", example = "102400")
        Long size,
        @Schema(description = "컨텐츠 타입", example = "image/png")
        String contentType
) {
}

package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "메시지 DTO")
public record MessageDto(
        @Schema(description = "메시지 ID", example = "9b0712f3-7dbf-4e9f-9f33-63cdb028b07a")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-03-10T10:15:30Z")
        Instant createdAt,
        @Schema(description = "수정 시각", example = "2026-03-10T10:20:00Z")
        Instant updatedAt,
        @Schema(description = "본문", example = "안녕하세요")
        String content,
        @Schema(description = "채널 ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
        UUID channelId,
        @Schema(description = "작성자")
        UserDto author,
        @Schema(description = "첨부 파일 목록")
        List<BinaryContentDto> attachments
) {
}

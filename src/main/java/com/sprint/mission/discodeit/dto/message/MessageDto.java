package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "메시지 응답")
public record MessageDto(
    @Schema(description = "메시지 ID", example = "4e23cb1a-e2ae-4171-811f-ccc4c13fc577")
    UUID id,
    @Schema(description = "메시지 생성 시간", example = "2026-02-23T01:30:54Z")
    Instant createdAt,
    @Schema(description = "메시지 수정 시간", example = "2026-02-23T01:30:54Z")
    Instant updatedAt,
    @Schema(description = "메시지 본문", example = "안녕하세요")
    String content,
    @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
    UUID channelId,
    @Schema(description = "메시지 작성자")
    UserDto author,
    @Schema(description = "첨부 파일 목록")
    List<BinaryContentDto> attachments
) {

}

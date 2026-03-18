package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "채널 DTO")
public record ChannelDto(
        @Schema(description = "채널 ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
        UUID id,
        @Schema(description = "채널 타입", example = "PUBLIC")
        ChannelType type,
        @Schema(description = "채널 이름", example = "공지")
        String name,
        @Schema(description = "채널 설명", example = "전체 공지 채널")
        String description,
        @Schema(description = "참여자 목록")
        List<UserDto> participants,
        @Schema(description = "마지막 메시지 시각", example = "2026-03-10T10:15:30Z")
        Instant lastMessageAt
) {
}

package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "채널 응답")
public record ChannelDto(
    @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
    UUID id,
    @Schema(description = "채널 타입", example = "PUBLIC")
    ChannelType type,
    @Schema(description = "채널 이름", example = "게임 채널")
    String name,
    @Schema(description = "채널 설명", example = "게임 채널입니다.")
    String description,
    @Schema(description = "채널 참여자 ID 목록")
    List<UserDto> participants,
    @Schema(description = "마지막 메시지 시각", example = "2026-02-23T01:30:54Z")
    Instant lastMessageAt
) {

}

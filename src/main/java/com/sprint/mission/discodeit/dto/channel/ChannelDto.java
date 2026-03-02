package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Channel 목록 응답 정보")
@JsonPropertyOrder({"id", "type", "name", "description", "participantIds", "lastMessageAt"})
public record ChannelDto(
        @Schema(description = "Channel ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
        UUID id,
        @Schema(description = "Channel 타입", example = "PUBLIC")
        ChannelType type,
        @Schema(description = "Channel 이름", example = "공지")
        String name,
        @Schema(description = "Channel 설명", example = "공지 채널")
        String description,
        @Schema(description = "참여자 ID 목록")
        Set<UUID> participantIds,
        @Schema(description = "마지막 메시지 시각", example = "2026-02-20T11:30:00Z")
        Instant lastMessageAt
) {
    public static ChannelDto of(Channel channel, Instant lastMessageAt) {
        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getMemberIds(),
                lastMessageAt
        );
    }
}

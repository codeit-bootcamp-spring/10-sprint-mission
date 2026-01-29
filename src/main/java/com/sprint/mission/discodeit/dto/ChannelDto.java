package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {
    public record CreatePublicRequest(
            @NotBlank
            String name,
            String description

    ) {}
    public record CreatePrivateRequest(
            List<UUID> memberIds

    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String channelName,
            String description,
            List<UUID> memberIds,
            Instant lastMessageAt
    ) {
        // factory method
        // 채널의 정보 + readStatus에서 구한 채널에 속한 멤버의 정보 + messageRepo에서 채널 내 메시지 중 가장 느린 메시지 생성 시간
        public static Response from(Channel channel, List<UUID> memberIds, Instant lastMessageAt ) {
            return new Response(
                    channel.getId(),
                    channel.getCreatedAt(),
                    channel.getUpdatedAt(),
                    channel.getType(),
                    channel.getName(),
                    channel.getDescription(),
                    memberIds,
                    lastMessageAt
            );
        }
    }

    public record UpdatePublicRequest(
            @NotBlank
            String newName,
            String newDescription

    ){}
}

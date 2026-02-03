package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelResponse {
    private UUID id;
    private String name;
    private String description;
    private ChannelType type;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastMessageTime; // 최근 메시지 시간
    private List<UUID> participantUserIds; // PRIVATE 채널 참여자 (PRIVATE일 때만)

    //Channel 엔티티를 DTO로 변환 (최근 메시지 시간, 참여자 정보 제외)
    public static ChannelResponse from (Channel channel) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .type(channel.getType())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .build();
    }

    //최근 메시지 시간 포함
    public static ChannelResponse from(Channel channel, Instant lastMessageTime) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .type(channel.getType())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .lastMessageTime().participantUserIDs()
                .build();
    }

    // 최근 메시지 시간 + 참여자 정보 포함 (PRIVATE 채널용)
    public static ChannelResponse from(Channel channel, Instant lastMessageTime, List<UUID> participantUserIds) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .type(channel.getType())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .lastMessageTime(lastMessageTime)
                .participantUserIds(participantUserIds)
                .build();
    }
}

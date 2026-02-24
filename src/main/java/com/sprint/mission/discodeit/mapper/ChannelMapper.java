package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelWithLastMessageDTO;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequestDTO;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequestDTO;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChannelMapper {
    public static Channel toPublicChannelEntity(CreatePublicChannelRequestDTO dto) {
        return new Channel(
                dto.name(),
                dto.description(),
                ChannelType.PUBLIC
        );
    }

    public static Channel toPrivateChannelEntity() {
        return new Channel(
                null,
                null,
                ChannelType.PRIVATE
        );
    }

    public static ChannelResponseDTO toResponse(Channel channel) {
        return new ChannelResponseDTO(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getChannelType(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.getJoinedUserIds(),
                channel.getCreatedAt()   // 채널 생성 시점이 마지막 메시지 시점이므로 createdAt으로 설정
        );
    }

    public static ChannelWithLastMessageDTO toWithLastMessage(
            Channel channel, Instant lastMessageAt
    ) {
        return new ChannelWithLastMessageDTO(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getChannelType(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.getJoinedUserIds(),
                lastMessageAt
        );
    }

    public static List<ChannelWithLastMessageDTO> toWithLastMessageList(
            List<Channel> channels
    ) {
        List<ChannelWithLastMessageDTO> dtos = new ArrayList<>();
        for (Channel channel: channels) {
            dtos.add(ChannelMapper.toWithLastMessage(channel, channel.getCreatedAt()));
        }

        return dtos;
    }
}

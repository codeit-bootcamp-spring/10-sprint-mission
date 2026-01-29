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
                dto.channelName(),
                ChannelType.PUBLIC
        );
    }

    public static Channel toPrivateChannelEntity(String channelName) {
        return new Channel(
                channelName,
                ChannelType.PRIVATE
        );
    }

    public static ChannelResponseDTO toResponse(Channel channel) {
        return new ChannelResponseDTO(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelType(),
                channel.getJoinedUserIds()
        );
    }

    public static ChannelWithLastMessageDTO toWithLastMessage(
            Channel channel, Instant lastMessageAt
    ) {
        return new ChannelWithLastMessageDTO(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelType(),
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

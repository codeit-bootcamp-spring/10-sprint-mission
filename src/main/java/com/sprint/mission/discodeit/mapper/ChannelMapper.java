package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChannelMapper {
    public static ChannelResponseDto channelToDto(Channel channel,Message lastMessage, List<UUID> memberIds) {
        return new ChannelResponseDto(channel.getId(),channel.getType(), channel.getName(),
                channel.getDescription()
                , channel.getCreatedAt()
                , channel.getUpdatedAt()
                ,lastMessage == null ?null:lastMessage.getCreatedAt()//아직 채널에 메세지가 없는 경우
                ,memberIds);
    }
}

package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@Component
public class ChannelMapper {
    public ChannelResponseDto toDto(Channel channel, Message lastMessage, List<UUID> memberIds) {
        return new ChannelResponseDto(channel.getId(),channel.getType(), channel.getName(),
                channel.getDescription()
                , channel.getCreatedAt()
                , channel.getUpdatedAt()
                ,lastMessage == null ?null:lastMessage.getCreatedAt()//아직 채널에 메세지가 없는 경우
                ,memberIds);
    }

    public Channel toEntity(PublicChannelCreateDto dto){
        return new Channel(ChannelType.PUBLIC, dto.name(), dto.description());
    }

    public Channel toEntity(PrivateChannelCreateDto dto){
        return new Channel(ChannelType.PRIVATE, null, null);
    }
}

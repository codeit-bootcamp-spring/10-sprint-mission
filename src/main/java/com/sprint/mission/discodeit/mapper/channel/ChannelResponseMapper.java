package com.sprint.mission.discodeit.mapper.channel;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface ChannelResponseMapper {

    @Mapping(source = "lastMessageTime", target = "lastMessageTime")
    ChannelResponseDto toDto(Instant lastMessageTime, Channel channel);
}

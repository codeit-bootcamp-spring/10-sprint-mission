package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

  @Mapping(target = "id", source = "channel.id")
  @Mapping(target = "type", source = "channel.type")
  @Mapping(target = "name", source = "channel.name")
  @Mapping(target = "description", source = "channel.description")
  @Mapping(target = "participants", source = "participants")
  @Mapping(target = "lastMessageAt", source = "lastMessageAt")
  ChannelDto toDto(Channel channel, List<UserDto> participants, Instant lastMessageAt);

  default ChannelDto toDto(Channel channel) {
    return toDto(channel, List.of(), null);
  }
}
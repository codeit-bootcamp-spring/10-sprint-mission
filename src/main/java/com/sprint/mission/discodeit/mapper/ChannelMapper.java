package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Mapping(target = "lastMessageAt", source = "lastMessageAt")
  @Mapping(target = "participants", source = "participants")
  public abstract ChannelDto toDto(
      Channel channel,
      Instant lastMessageAt,
      List<UserDto> participants);
}

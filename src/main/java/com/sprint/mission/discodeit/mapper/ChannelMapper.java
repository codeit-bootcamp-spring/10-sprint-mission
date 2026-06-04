package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class, uses = {UserMapper.class})
public interface ChannelMapper {

  @Mapping(target = "lastMessageAt", source = "lastMessageAt")
  @Mapping(target = "participants", source = "participants")
  ChannelDto toDto(
      Channel channel,
      Instant lastMessageAt,
      List<UserDto> participants);
}

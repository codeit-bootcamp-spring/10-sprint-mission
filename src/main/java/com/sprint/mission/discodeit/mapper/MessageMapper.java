package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class, uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

  @Mapping(target = "channelId", source = "channel.id")
  MessageDto toDto(Message message);
}

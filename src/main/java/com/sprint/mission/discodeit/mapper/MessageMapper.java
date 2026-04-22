package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.config.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class, uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper extends BaseMapper<Message, MessageDto>,
    PageResponseMapper<Message, MessageDto> {

  @Override
  @Mapping(target = "channelId", source = "channel.id")
  MessageDto toDto(Message entity);

  Message partialUpdate(MessageUpdateRequest request, @MappingTarget Message message);
}

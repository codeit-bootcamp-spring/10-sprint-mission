package com.sprint.mission.discodeit.message.mapper;

import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.common.mapper.BaseMapper;
import com.sprint.mission.discodeit.common.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.dto.response.MessageResponse;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class, uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper extends BaseMapper<Message, MessageResponse>,
    PageResponseMapper<Message, MessageResponse> {

  @Override
  @Mapping(target = "channelId", source = "channel.id")
  MessageResponse toDto(Message entity);

  Message partialUpdate(MessageUpdateRequest request, @MappingTarget Message message);
}

package com.sprint.mission.discodeit.message.presentation.mapper;

import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentApiMapper;
import com.sprint.mission.discodeit.common.mapper.GenericApiMapper;
import com.sprint.mission.discodeit.common.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.presentation.dto.response.MessageResponse;
import com.sprint.mission.discodeit.user.presentation.mapper.UserApiMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class, uses = {UserApiMapper.class,
    BinaryContentApiMapper.class})
public interface MessageApiMapper extends GenericApiMapper<Message, MessageResponse>,
    PageResponseMapper<Message, MessageResponse> {

  @Override
  @Mapping(target = "channelId", source = "channel.id")
  MessageResponse toDto(Message entity);
}

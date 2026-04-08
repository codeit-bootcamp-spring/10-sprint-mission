package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.config.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class, uses = BinaryContentMapper.class)
public interface MessageMapper extends BaseMapper<Message, MessageDto> {

  Message partialUpdate(MessageUpdateRequest request, @MappingTarget Message message);
}

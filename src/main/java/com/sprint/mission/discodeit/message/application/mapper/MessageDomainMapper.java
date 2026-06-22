package com.sprint.mission.discodeit.message.application.mapper;

import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.common.mapper.util.UtilMapper;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageUpdatedEvent;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class, uses = UtilMapper.class)
public interface MessageDomainMapper {

  void partialUpdate(MessageUpdateRequest request, @MappingTarget Message entity);

  @Mapping(target = "authorId", source = "author.id")
  @Mapping(target = "channelId", source = "channel.id")
  @Mapping(target = "attachmentIds", source = "attachments", qualifiedByName = "extractIds")
  MessageCreatedEvent toCreatedEvent(Message message);

  @Mapping(target = "channelId", source = "channel.id")
  MessageUpdatedEvent toUpdatedEvent(Message message);

  @Mapping(target = "channelId", source = "channel.id")
  MessageDeletedEvent toDeletedEvent(Message message);
}

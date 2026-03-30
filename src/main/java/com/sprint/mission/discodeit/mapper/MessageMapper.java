package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {

  @Mapping(target = "author", source = "author")
  @Mapping(target = "attachments", source = "attachments")
  @Mapping(target = "channelId", source = "channel.id")
  MessageDto toDto(Message message);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "attachments", ignore = true)
  @Mapping(target = "content", source = "request.content")
  Message toEntity(MessageCreateRequest request, Channel channel, User author);
}

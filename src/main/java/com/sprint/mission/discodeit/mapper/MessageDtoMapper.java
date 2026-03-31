package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageDtoMapper {

  @Mapping(source = "message.id", target = "id")
  @Mapping(source = "message.createdAt", target = "createdAt")
  @Mapping(source = "message.updatedAt", target = "updatedAt")
  @Mapping(source = "message.content", target = "content")
  @Mapping(source = "message.channel.id", target = "channelId")
  @Mapping(source = "author", target = "author")
  @Mapping(source = "attachments", target = "attachments")
  MessageDto toDto(Message message, UserDto author, List<BinaryContentDto> attachments);
}
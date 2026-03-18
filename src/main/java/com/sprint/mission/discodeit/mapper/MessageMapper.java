package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "attachmentIds", source = "attachments")
    MessageDto toDto(Message message);
}

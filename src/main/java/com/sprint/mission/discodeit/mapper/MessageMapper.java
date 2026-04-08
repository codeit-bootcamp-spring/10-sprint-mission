package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public abstract class MessageMapper {

    @Mapping(source = "channel.id", target = "channelId")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "attachments", target = "attachments")
    public abstract MessageDto toDto(Message message);
}
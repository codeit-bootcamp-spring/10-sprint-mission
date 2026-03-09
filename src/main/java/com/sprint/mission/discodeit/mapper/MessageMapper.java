package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

    // MessageDto -> channelId / author / attachments
    @Mapping(target="channelId", source = "channel.id")
    @Mapping(target="author", source = "author")
    @Mapping(target="attachments", source = "attachments")
    MessageDto toDto(Message message);
}
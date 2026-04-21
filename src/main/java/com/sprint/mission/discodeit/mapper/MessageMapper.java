package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring", uses = {UserMapper.class,BinaryContentMapper.class})
public abstract class MessageMapper {
    @Autowired
    protected BinaryContentMapper binaryContentMapper;

    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "author.online", constant = "true")
    public abstract MessageDto toDto(Message message);

}

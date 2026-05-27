package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public abstract class MessageMapper {

    @Autowired
    protected UserMapper userMapper;

    @Mapping(source = "channel.id", target = "channelId")
    public abstract MessageDto toDto(Message message);

    @Mapping(source = "message.channel.id", target = "channelId")
    @Mapping(target = "author", expression = "java(userMapper.toDto(message.getAuthor(), onlineUserIds))")
    public abstract MessageDto toDto(Message message, Set<UUID> onlineUserIds);

}

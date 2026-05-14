package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class ChannelMapper {

    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "id", source = "channel.id")
    @Mapping(target = "type", source = "channel.type")
    @Mapping(target = "name", source = "channel.name")
    @Mapping(target = "description", source = "channel.description")
    @Mapping(target = "participants", expression = "java(mapParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(resolveLastMessageAt(channel, lastMessage))")
    public abstract ChannelDto toDto(Channel channel, Message lastMessage);

    protected List<UserDto> mapParticipants(Channel channel) {

        return channel.getReadStatuses()
            .stream()
            .map(ReadStatus::getUser)
            .filter(Objects::nonNull)
            .map(userMapper::toDto)
            .toList();
    }

    protected Instant resolveLastMessageAt(Channel channel, Message lastMessage) {
        return lastMessage != null ? lastMessage.getCreatedAt() : channel.getCreatedAt();
    }
}

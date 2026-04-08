package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ChannelMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageRepository messageRepository;

    @Mapping(target = "type", constant = "PUBLIC")
    public abstract Channel toEntity(PublicChannelPostDto publicChannelPostDto);

    @Mapping(target = "type", constant = "PRIVATE")
    public abstract Channel toEntity(PrivateChannelPostDto privateChannelPostDto);

    @Mapping(target = "participants", expression = "java(getParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(getLastMessageAt(channel))")
    public abstract ChannelDto toDto(Channel channel);

    Instant getLastMessageAt(Channel channel) {
        return channel.getMessageList().stream()
            .map(Message::getCreatedAt)
            .max(Instant::compareTo)
            .orElse(null);
    }

    public List<UserDto> getParticipants(Channel channel) {
        return channel.getReadStatusList().stream()
            .map(ReadStatus::getUser)
            .map(userMapper::toDto)
            .toList();
    }

}

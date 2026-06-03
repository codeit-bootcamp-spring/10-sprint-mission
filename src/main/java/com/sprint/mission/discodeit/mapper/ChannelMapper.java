package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(assignParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(assignLastMessageAt(channel))")
    public abstract ChannelDto toDto(Channel channel);

    // `@Mapping`은 추상 메서드에서만 사용 가능
    public ChannelDto toListDto(
            Channel channel,
            Map<UUID, List<UserDto>> participantMap,
            Map<UUID, Instant> lastMessageAtMap
    ) {
        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getType().equals(ChannelType.PRIVATE)
                        ? participantMap.getOrDefault(channel.getId(), List.of())
                        : List.of(),
                lastMessageAtMap.getOrDefault(channel.getId(), null)
        );
    }

    protected List<UserDto> assignParticipants(Channel channel) {
        List<UserDto> participants = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelIdWithUserAndChannel(channel.getId()).stream()
                    .map(readStatus ->
                            userMapper.toDto(readStatus.getUser())
                    )
                    .forEach(userDto -> participants.add(userDto));
        }
        return participants;
    }

    protected Instant assignLastMessageAt(Channel channel) {
        return messageRepository.findLastMessageAtByChannelId(channel.getId())
                .orElse(null);
    }
}

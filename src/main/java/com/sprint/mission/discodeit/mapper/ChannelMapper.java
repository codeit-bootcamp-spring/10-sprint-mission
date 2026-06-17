package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.security.JwtRegistry;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

    @Autowired
    protected MessageRepository messageRepository;

    @Autowired
    protected ReadStatusRepository readStatusRepository;

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected JwtRegistry jwtRegistry;

    @Mapping(target = "lastMessageAt", expression = "java(getLastMessageAt(channel))")
    @Mapping(target = "participants", expression = "java(getParticipants(channel))")
    public abstract ChannelDto toDto(Channel channel);

    @Mapping(target = "lastMessageAt", expression = "java(lastMessageMap != null ? lastMessageMap.get(channel.getId()) : null)")
    @Mapping(target = "participants", expression = "java(participantsMap != null ? participantsMap.getOrDefault(channel.getId()," +
            " java.util.Collections.emptyList()) : java.util.Collections.emptyList())")
    public abstract ChannelDto toDtoWithContext(Channel channel,
                                                @Context Map<UUID, Instant> lastMessageMap,
                                                @Context Map<UUID, List<UserDto>> participantsMap);

    protected Instant getLastMessageAt(Channel channel) {
        return messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    protected List<UserDto> getParticipants(Channel channel) {
        return readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .filter(rs -> rs.getChannel().getId().equals(channel.getId()))
                .map(rs -> userMapper.toDto(rs.getUser(),
                        jwtRegistry.hasActiveJwtInformationByUserId(rs.getUser().getId())))
                .toList();
    }
}
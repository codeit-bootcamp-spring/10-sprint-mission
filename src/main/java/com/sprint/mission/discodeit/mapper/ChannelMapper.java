package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ChannelMapper {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel.getId())
                .map(BaseEntity::getCreatedAt)
                .orElse(null);

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                getParticipants(channel.getType(), channel.getId()),
                lastMessageAt
        );
    }

    public List<ChannelDto> toDto(List<ChannelDto.RemoveParticipants> channels) {
        return channels.stream()
                .map(c -> new ChannelDto(
                        c.id(),
                        c.type(),
                        c.name(),
                        c.description(),
                        getParticipants(c.type(), c.id()),
                        c.lastMessageAt()
                ))
                .toList();
    }

    List<UserDto> getParticipants(ChannelType type, UUID channelId) {
        return type == ChannelType.PRIVATE
                ? readStatusRepository.findAllByChannelId(channelId).stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toDto)
                    .toList()
                : new ArrayList<>();
    }
}

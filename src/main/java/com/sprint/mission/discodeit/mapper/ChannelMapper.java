package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.ChannelSummary;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ChannelMapper {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private UserMapper userMapper;

  public ChannelDto toDto(Channel channel) {
    Instant lastMessageAt = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(
            channel.getId())
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

  public List<ChannelDto> toDto(
      List<ChannelSummary> channels,
      Map<UUID, List<UserDto>> participants) {
    return channels.stream()
        .map(c -> new ChannelDto(
            c.id(),
            c.type(),
            c.name(),
            c.description(),
            participants.getOrDefault(c.id(), Collections.emptyList()),
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

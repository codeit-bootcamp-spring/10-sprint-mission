package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Autowired
  protected MessageRepository messageRepository;

  @Autowired
  protected ReadStatusRepository readStatusRepository;

  @Autowired
  protected UserMapper userMapper;

  @Mapping(target = "lastMessageAt", expression = "java(fetchLastMessageAt(channel))")
  @Mapping(target = "participants", expression = "java(fetchParticipants(channel))")
  public abstract ChannelDto toDto(Channel channel);

  protected Instant fetchLastMessageAt(Channel channel) {
    return messageRepository.findLastMessageAtByChannelId(channel.getId())
        .orElse(null);
  }

  protected java.util.List<com.sprint.mission.discodeit.dto.user.UserDto> fetchParticipants(Channel channel) {
    return readStatusRepository.findAllByChannelId(channel.getId())
      .stream()
      .map(readStatus -> userMapper.toDto(readStatus.getUser()))
      .toList();
  }
}

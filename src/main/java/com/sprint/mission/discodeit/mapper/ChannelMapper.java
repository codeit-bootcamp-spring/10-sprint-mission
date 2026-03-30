package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;
  @Autowired
  private UserMapper userMapper;

  @Mapping(target = "lastMessageAt", expression = "java(getLastMessageAt(channel))")
  @Mapping(target = "participants", expression = "java(getParticipants(channel))")
  public abstract ChannelDto toDto(Channel channel);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "type", constant = "PUBLIC")
  public abstract Channel toEntity(PublicChannelCreateRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "type", constant = "PRIVATE")
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "description", ignore = true)
  public abstract Channel toEntity(PrivateChannelCreateRequest request);

  protected Instant getLastMessageAt(Channel channel) {
    return messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel)
        .map(Message::getCreatedAt)
        .orElse(null);
  }

  protected List<UserDto> getParticipants(Channel channel) {
    return readStatusRepository.findAllByChannel(channel).stream()
        .map(ReadStatus::getUser)
        .map(userMapper::toDto)
        .toList();
  }
}

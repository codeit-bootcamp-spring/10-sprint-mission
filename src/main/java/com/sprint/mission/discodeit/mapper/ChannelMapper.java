package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.UserSessionManager;
import java.time.Instant;
import java.util.Set;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class ChannelMapper {

  @Autowired
  protected UserMapper userMapper;
//  @Autowired
//  protected ReadStatusRepository readStatusRepository;
//  @Autowired
//  protected MessageRepository messageRepository;
//  @Autowired
//  protected UserSessionManager userSessionManager;

//
//  public ChannelDto toDto(Channel channel) {
//    Message lastMessage = messageRepository.findLastMessageByChannelId(channel.getId())
//        .orElse(null);
//    Set<UUID> onlineUserIds = userSessionManager.getOnlineUserIds();
//    return new ChannelDto(
//        channel.getId(),
//        channel.getType(),
//        channel.getName(),
//        channel.getDescription(),
//        channel.getCreatedAt(),
//        channel.getUpdatedAt(),
//        lastMessage == null ? null : lastMessage.getCreatedAt(),//아직 채널에 메세지가 없는 경우 null
//        readStatusRepository.findAllByChannelIdFetchUser(channel.getId()).stream()//유저 정보 같이 가져오기
//            .map(s -> userMapper.toDto(s.getUser(), onlineUserIds.contains(s.getUser().getId())))
//            .toList()
//    );
//  }

  public Channel toEntity(PublicChannelCreateRequest dto) {
    return Channel.create(ChannelType.PUBLIC, dto.name(), dto.description());
  }

  public Channel toEntity(PrivateChannelCreateRequest dto) {
    return Channel.create(ChannelType.PRIVATE, null, null);
  }

  public ChannelDto toDto(Channel channel, List<User> users, Instant lastMessageAt,
      Set<UUID> onlineUserIds) {
    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        channel.getCreatedAt(),
        channel.getUpdatedAt(),
        lastMessageAt, //아직 채널에 메세지가 없는 경우 null
        users.stream().map(user -> userMapper.toDto(user, onlineUserIds.contains(user.getId())))
            .toList()
    );
  }

  public ChannelDto toDto(ChannelDto channel, List<UserDto> users, Instant lastMessageAt,
      Set<UUID> onlineUserIds) {
    return new ChannelDto(
        channel.id(),
        channel.type(),
        channel.name(),
        channel.description(),
        channel.createdAt(),
        channel.updatedAt(),
        lastMessageAt, //아직 채널에 메세지가 없는 경우 null
        users.stream().map(user -> userMapper.toDto(user, onlineUserIds.contains(user.id())))
            .toList()
    );
  }

}

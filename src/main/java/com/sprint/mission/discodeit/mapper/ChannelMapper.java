package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

  @Mapping(source = "channel.id", target = "channelId")
  @Mapping(expression = "java(channel.getChannelName())", target = "channelName")
  @Mapping(source = "channel.description", target = "description")
  @Mapping(expression = "java(channel.isPrivate())", target = "isPrivate")
  @Mapping(source = "lastMessageTime", target = "lastMessageTime")
  @Mapping(source = "participantIds", target = "participantIds")
  ChannelResponse toResponse(Channel channel, Instant lastMessageTime, List<UUID> participantIds);

  @Mapping(source = "channel.id", target = "id")
  @Mapping(expression = "java(channel.isPrivate() ? com.sprint.mission.discodeit.dto.channel.ChannelDocResponse.ChannelType.PRIVATE : com.sprint.mission.discodeit.dto.channel.ChannelDocResponse.ChannelType.PUBLIC)", target = "type")
  @Mapping(expression = "java(channel.getChannelName())", target = "name")
  @Mapping(source = "channel.description", target = "description")
  @Mapping(source = "participants", target = "participants")
  @Mapping(source = "lastMessageAt", target = "lastMessageAt")
  ChannelDto toDto(Channel channel, List<User> participants, Instant lastMessageAt);
}
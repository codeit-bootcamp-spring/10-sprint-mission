package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ChannelMapper {

  public Channel toChannel(PublicChannelPostDto publicChannelPostDto) {
    return new Channel(
        ChannelType.PUBLIC,
        publicChannelPostDto.name(),
        publicChannelPostDto.description()
    );
  }

  public Channel toChannel(PrivateChannelPostDto privateChannelPostDto) {
    Channel channel = new Channel(
        ChannelType.PRIVATE,
        "",
        ""
    );

    for (UUID userId : privateChannelPostDto.participantIds()) {
      channel.addUserId(userId);
    }

    return channel;
  }

  // public Channel toChannel(Private)

  public ChannelResponseDto fromChannel(Channel channel, Instant lastMessageTime) {
    return new ChannelResponseDto(
        channel.getId(),
        channel.getCreatedAt(),
        channel.getUpdatedAt(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        channel.getType() == ChannelType.PRIVATE ? channel.getUserIds() : null,
        lastMessageTime
    );
  }

}

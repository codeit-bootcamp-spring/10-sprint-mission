package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDtoResponse(
    UUID id,
    ChannelDocResponse.ChannelType type,
    String name,
    String description,
    List<UUID> participantIds,
    Instant lastMessageAt
) {

  public static ChannelDtoResponse from(ChannelResponse r) {
    return new ChannelDtoResponse(
        r.channelId(),
        r.isPrivate() ? ChannelDocResponse.ChannelType.PRIVATE
            : ChannelDocResponse.ChannelType.PUBLIC,
        r.channelName(),
        r.description(),
        r.participantIds(),
        r.lastMessageTime()
    );
  }
}

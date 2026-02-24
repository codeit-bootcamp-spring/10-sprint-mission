package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.data.Channel.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UUID> participantIds,
    Instant lastMessageAt
) {

  @SuppressWarnings("unchecked")
  public static ChannelResponse from(ChannelDto dto) {
    return new ChannelResponse(
        dto.id(),
        dto.type(),
        dto.name(),
        dto.description(),
        (List<UUID>) dto.participantIds(),
        dto.lastMessageAt()
    );
  }
}

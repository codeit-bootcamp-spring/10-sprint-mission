package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record ChannelDocResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    ChannelType type,
    String name,
    String description
) {

  public enum ChannelType {
    PUBLIC, PRIVATE
  }

  public static ChannelDocResponse of(UUID id, Instant createdAt, Instant updatedAt,
      boolean isPrivate,
      String name, String description) {
    return new ChannelDocResponse(
        id,
        createdAt,
        updatedAt,
        isPrivate ? ChannelType.PRIVATE : ChannelType.PUBLIC,
        name,
        description
    );
  }
}

package com.sprint.mission.discodeit.dto.sse;

import java.util.UUID;

public record SseMessage(
    UUID id,
    String name,
    Object data
) {

  public static SseMessage of(String name, Object data) {
    return new SseMessage(UUID.randomUUID(), name, data);
  }

}

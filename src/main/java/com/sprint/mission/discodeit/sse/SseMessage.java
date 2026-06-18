package com.sprint.mission.discodeit.sse;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

public record SseMessage(
    UUID id,
    String eventName,
    Object data,
    Collection<UUID> receiverIds,
    Instant createdAt
) {
  public static SseMessage of(String eventName, Object data, Collection<UUID> receiverIds) {
    return new SseMessage(UUID.randomUUID(), eventName, data, receiverIds, Instant.now());
  }
}

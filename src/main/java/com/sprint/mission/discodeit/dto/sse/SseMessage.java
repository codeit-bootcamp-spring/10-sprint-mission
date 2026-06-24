package com.sprint.mission.discodeit.dto.sse;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public record SseMessage(
    UUID id,
    String eventName,
    Object data,
    Set<UUID> receiverIds
) {

  public static SseMessage of(String eventName, Object data, Collection<UUID> receiverIds) {
    Set<UUID> targets = (receiverIds == null) ? Set.of() : Set.copyOf(receiverIds);
    return new SseMessage(null, eventName, data, targets);
  }

  public static SseMessage broadcast(String eventName, Object data) {
    return new SseMessage(null, eventName, data, Set.of());
  }

  public SseMessage withId(UUID newId) {
    return new SseMessage(newId, eventName, data, receiverIds);
  }

  public boolean isBroadcast() {
    return receiverIds == null || receiverIds.isEmpty();
  }

  public boolean isTargetedTo(UUID receiverId) {
    return isBroadcast() || receiverIds.contains(receiverId);
  }
}

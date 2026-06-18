package com.sprint.mission.discodeit.sse;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SseMessage {

  private final UUID id;
  private Set<UUID> receiverIds;
  private String eventName;
  private Object data;
  private boolean isBroadcast;

  public static SseMessage create(Collection<UUID> receiverIds, String eventName,
      Object data,
      boolean isBroadcast) {
    Set<UUID> setReceiverIds = (receiverIds == null)
        ? new HashSet<>()
        : new HashSet<>(receiverIds);
    return new SseMessage(UUID.randomUUID(), setReceiverIds, eventName, data,
        isBroadcast);
  }
}

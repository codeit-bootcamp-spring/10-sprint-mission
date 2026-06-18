package com.sprint.mission.discodeit.dto.data;

import java.util.Set;
import java.util.UUID;

public record SseMessage(
    UUID id,
    Set<UUID> receiverIds,
    String eventName,
    Object data
) {

  public boolean isBroadcast() {
    return receiverIds == null || receiverIds.isEmpty();
  }

  public boolean canReceive(UUID receiverId) {
    return isBroadcast() || receiverIds.contains(receiverId);
  }
}

package com.sprint.mission.discodeit.dto.data;

import java.util.Set;
import java.util.UUID;

public record SseMessage(
    UUID id,
    String eventName,
    Object data,
    Set<UUID> receiverIds,
    boolean broadcast
) {
}

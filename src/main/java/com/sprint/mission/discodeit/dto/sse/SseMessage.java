package com.sprint.mission.discodeit.dto.sse;

import java.util.Set;
import java.util.UUID;

public record SseMessage(
    UUID eventId,
    String eventName,
    Object data,
    Set<UUID> receiverIds,
    boolean broadcast
) {

}

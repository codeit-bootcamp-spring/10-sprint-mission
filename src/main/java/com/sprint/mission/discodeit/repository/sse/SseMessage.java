package com.sprint.mission.discodeit.repository.sse;

import java.util.Collection;
import java.util.UUID;

public record SseMessage(
        UUID id,
        String eventName,
        Object data,
        Collection<UUID> receiverIds,
        boolean broadcast
) {
    public boolean matches(UUID receiverId) {
        return broadcast || (receiverIds != null && receiverIds.contains(receiverId));
    }
}

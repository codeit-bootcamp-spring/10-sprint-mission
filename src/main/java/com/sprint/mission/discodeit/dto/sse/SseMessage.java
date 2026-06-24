package com.sprint.mission.discodeit.dto.sse;

import java.util.Collection;
import java.util.UUID;

public record SseMessage(
        UUID id,
        Collection<UUID> receiverIds,
        String eventName,
        Object data
) {
}

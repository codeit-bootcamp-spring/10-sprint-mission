package com.sprint.mission.discodeit.dto.data;

import java.util.Set;
import java.util.UUID;

public record SseMessage(
        UUID id,
        Set<UUID> receiverIds,
        String eventName,
        Object data
) {

    public static SseMessage of(Set<UUID> receiverIds, String eventName, Object data) {
        return new SseMessage(UUID.randomUUID(), receiverIds, eventName, data);
    }
}

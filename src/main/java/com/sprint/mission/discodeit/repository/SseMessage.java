package com.sprint.mission.discodeit.repository;

import java.util.UUID;

public record SseMessage(
    UUID id,
    UUID receiverId,
    String eventName,
    Object data
) {
}

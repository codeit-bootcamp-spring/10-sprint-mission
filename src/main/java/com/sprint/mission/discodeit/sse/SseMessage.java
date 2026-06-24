package com.sprint.mission.discodeit.sse;

import java.util.UUID;

public record SseMessage(UUID id, UUID receiverId, String name, Object data) {

}
package com.sprint.mission.discodeit.event.kafka;

import java.util.List;
import java.util.UUID;

public record SseFanoutMessage(String eventName, Object data, List<UUID> receiverIds) {
}

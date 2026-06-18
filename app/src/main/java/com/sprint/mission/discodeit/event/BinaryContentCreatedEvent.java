package com.sprint.mission.discodeit.event;

import java.util.Collection;
import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes,
    Collection<UUID> receiverIds
) {

}

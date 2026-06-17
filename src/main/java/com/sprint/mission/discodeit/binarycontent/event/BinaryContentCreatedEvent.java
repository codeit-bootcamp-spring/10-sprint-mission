package com.sprint.mission.discodeit.binarycontent.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {

}

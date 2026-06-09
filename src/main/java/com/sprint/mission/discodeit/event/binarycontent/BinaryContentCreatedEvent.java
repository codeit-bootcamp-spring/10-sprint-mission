package com.sprint.mission.discodeit.event.binarycontent;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] data
) {

}

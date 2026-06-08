package com.sprint.mission.discodeit.events;

import java.util.UUID;

public record AdminBinaryContentUploadFailedEvent(
    Long requestId,
    UUID binaryContentId,
    String message
) {

}

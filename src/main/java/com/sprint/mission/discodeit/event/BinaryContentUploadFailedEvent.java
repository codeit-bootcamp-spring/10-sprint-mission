package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentUploadFailedEvent(
    String requestId,
    UUID binaryContentId,
    String errorMessage
) {

}

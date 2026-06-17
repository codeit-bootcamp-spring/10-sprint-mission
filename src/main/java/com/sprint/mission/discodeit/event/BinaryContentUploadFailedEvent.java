package com.sprint.mission.discodeit.event;

public record BinaryContentUploadFailedEvent(
    String requestId,
    String contentId,
    String error
) {

}

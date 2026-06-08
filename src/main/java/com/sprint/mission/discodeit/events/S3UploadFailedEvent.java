package com.sprint.mission.discodeit.events;

import java.util.UUID;

public record S3UploadFailedEvent(
    UUID binaryContentId
) {

}

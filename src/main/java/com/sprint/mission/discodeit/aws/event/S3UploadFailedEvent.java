package com.sprint.mission.discodeit.aws.event;

public record S3UploadFailedEvent(
    String filename,
    String reason
) {

}

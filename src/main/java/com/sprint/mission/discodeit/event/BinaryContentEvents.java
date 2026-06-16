package com.sprint.mission.discodeit.event;

import java.util.UUID;

public final class BinaryContentEvents {
    private BinaryContentEvents() {}

    public record Created(UUID binaryContentId, byte[] bytes) {}
    public record S3UploadFailed(UUID binaryContentId, String mdcRequestId, String errorMessage) {}
}

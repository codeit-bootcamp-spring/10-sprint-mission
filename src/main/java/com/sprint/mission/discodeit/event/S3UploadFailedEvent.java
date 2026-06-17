package com.sprint.mission.discodeit.event;

import java.util.UUID;

// S3 업로드 재시도가 모두 실패했을 때
public record S3UploadFailedEvent(
    String taskName, String requestId, UUID binaryContentId, String errorMessage) {}

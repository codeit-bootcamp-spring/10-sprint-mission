package com.sprint.mission.discodeit.event.kafka;

import java.util.UUID;

// Kafka 전용 DTO
public record S3UploadFailedKafkaPayload(
    UUID binaryContentId,
    String requestId,
    String reason // Throwable 대신 메시지 문자열만 추출
) {

}

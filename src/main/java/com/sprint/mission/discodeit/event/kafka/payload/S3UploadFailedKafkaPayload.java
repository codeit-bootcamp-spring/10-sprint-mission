package com.sprint.mission.discodeit.event.kafka.payload;

import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import java.util.UUID;

// 현재 S3UploadFailedEvent에서 Throwable exception를 활용하고 있음
// Throwable 전체를 Kafka로 보내지 않고, 알림 생성에 필요한 안전한 문자열 정보만 전달하기 위한 클래스

public record S3UploadFailedKafkaPayload(
    UUID binaryContentId,
    String exceptionType,
    String errorMessage,
    String requestId
) {

  public static S3UploadFailedKafkaPayload from(S3UploadFailedEvent event) {
    Throwable exception = event.exception();

    return new S3UploadFailedKafkaPayload(
        event.binaryContentId(),
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        event.requestId()
    );
  }
}

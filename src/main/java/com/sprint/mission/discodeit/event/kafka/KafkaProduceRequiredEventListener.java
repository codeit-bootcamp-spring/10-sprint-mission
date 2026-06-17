package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.kafka.payload.S3UploadFailedKafkaPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  // MessageCreatedEvent를 Spring Event에서 Kafka 메시지로 중계
  // 메인 서비스와 분리된 알림 서비스가 외부 브로커에서 메시지 생성 이벤트를 소비할 수 있게 함
  // 현재 구조는 DB 커밋 후 Kafka 발행 실패 시 이벤트 유실 가능성 있음 -> Outbox Pattern으로 보완 가능
  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    send(
        KafkaTopics.MESSAGE_CREATED,
        event.messageId().toString(),
        event
    );
  }

  // RoleUpdatedEvent를 Spring Event에서 Kafka 메시지로 중계
  // 권한 변경 알림을 별도 알림 서비스에서 생성
  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    send(
        KafkaTopics.ROLE_UPDATED,
        event.userId().toString(),
        event
    );
  }

  // S3 업로드 최종 실패 이벤트를 Kafka 메시지로 중계
  // S3 업로드 실패를 관리자 알림으로 전달
  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    S3UploadFailedKafkaPayload payload = S3UploadFailedKafkaPayload.from(
        event); // Throwable은 Kafka에서 활용하기 부적절하므로 payload로 변환

    send(
        KafkaTopics.S3_UPLOAD_FAILED,
        event.binaryContentId().toString(),
        payload
    );
  }

  private void send(String topic, String key, Object event) {
    try {
      String payload = objectMapper.writeValueAsString(event);

      kafkaTemplate.send(topic, key, payload)
          .whenComplete((result, ex) -> {
            if (ex != null) {
              log.error("Kafka 이벤트 발행 실패: topic={}, key={}", topic, key, ex);
              return;
            }

            log.info(
                "Kafka 이벤트 발행 성공: topic={}, key={}, partition={}, offset={}",
                topic,
                key,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
            );
          });
    } catch (JsonProcessingException e) {
      log.error("Kafka 이벤트 직렬화 실패: topic={}, key={}, eventType={}",
          topic, key, event.getClass().getSimpleName(), e);
      throw new IllegalStateException("Kafka 이벤트 직렬화 실패", e);
    }
  }
}

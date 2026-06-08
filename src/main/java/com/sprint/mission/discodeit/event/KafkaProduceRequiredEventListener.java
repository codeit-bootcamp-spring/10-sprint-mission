package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  private static final String MESSAGE_CREATED_TOPIC = "discodeit.MessageCreatedEvent";
  private static final String ROLE_UPDATED_TOPIC = "discodeit.RoleUpdatedEvent";
  private static final String S3_UPLOAD_FAILED_TOPIC = "discodeit.S3UploadFailedEvent";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    send(MESSAGE_CREATED_TOPIC, event.messageId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    send(ROLE_UPDATED_TOPIC, event.receiverId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    send(S3_UPLOAD_FAILED_TOPIC, event.binaryContentId().toString(), event);
  }

  private void send(String topic, String key, Object event) {
    try {
      String payload = objectMapper.writeValueAsString(event);

      kafkaTemplate.send(topic, key, payload);

      log.debug("Kafka 이벤트 발행 완료: topic={}, key={}, payload={}", topic, key, payload);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Kafka 이벤트 직렬화 실패: " + event.getClass().getSimpleName(), e);
    }
  }
}

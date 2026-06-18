package com.sprint.mission.discodeit.util.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.ErrorNotificationEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
    name = "notification-type.kafka.enabled",
    havingValue = "true"
)
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("notificationTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    log.debug("MessageCreatedEvent 수신 하여 Kafka 발행");
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent 직렬화 실패", e);
      throw new RuntimeException(e);
    }

  }

  @Async("notificationTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    log.debug("RoleUpdatedEvent 수신하여 Kafka 발행");
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent 직렬화 실패", e);
      throw new RuntimeException(e);
    }
  }

  @Async("notificationTaskExecutor")
  @EventListener
  public void on(ErrorNotificationEvent event) {
    log.debug("ErrorNotificationEvent 수신하여 Kafka 발행");
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.ErrorNotificationEvent", payload);
    } catch (JsonProcessingException e) {
      log.error("ErrorNotificationEvent 직렬화 실패", e);
      throw new RuntimeException(e);
    }
  }
}
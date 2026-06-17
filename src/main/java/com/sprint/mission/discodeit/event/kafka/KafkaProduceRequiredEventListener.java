package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventExecutor")
  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void on(MessageCreatedEvent event){
    try{
      String payload = objectMapper.writeValueAsString(event);

      kafkaTemplate.send(
          "discodeit.MessageCreatedEvent",
          payload
      );

      log.info("Kafka 이벤트 발행 성공: MessageCreatedEvent");

    }catch(Exception e){
      log.error("Kafka 이벤트 발행 실패: MessageCreatedEvent");
    }
  }

  @Async("eventExecutor")
  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void on(RoleUpdatedEvent event) {
    try{
      String payload = objectMapper.writeValueAsString(event);

      kafkaTemplate.send(
          "discodeit.RoleUpdatedEvent",
          payload
      );

      log.info("Kafka 이벤트 발행 성공: RoleUpdatedEvent");

    }catch(JsonProcessingException e){
      log.error("Kafka 이벤트 발행 실패: RoleUpdatedEvent");
    }
  }

  @Async("eventExecutor")
  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void on(S3UploadFailedEvent event) {
    try{
      String payload = objectMapper.writeValueAsString(event);

      kafkaTemplate.send(
          "discodeit.S3UploadFailedEvent",
          payload
      );

      log.info("Kafka 이벤트 발행 성공: S3UploadFailedEvent");

    }catch(JsonProcessingException e){
      log.error("Kafka 이벤트 발행 실패: S3UploadFailedEvent");
    }
  }
}
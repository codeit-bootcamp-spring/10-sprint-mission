package com.sprint.mission.discodeit.eventlisteners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.events.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.events.ChannelCreatedEvent;
import com.sprint.mission.discodeit.events.ChannelDeletedEvent;
import com.sprint.mission.discodeit.events.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.events.MessageCreatedEvent;
import com.sprint.mission.discodeit.events.RoleUpdatedEvent;
import com.sprint.mission.discodeit.events.S3UploadFailedEvent;
import com.sprint.mission.discodeit.events.UserCreatedEvent;
import com.sprint.mission.discodeit.events.UserDeletedEvent;
import com.sprint.mission.discodeit.events.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
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

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) throws JsonProcessingException {
    send("discodeit.MessageCreatedEvent", event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) throws JsonProcessingException {
    send("discodeit.RoleUpdatedEvent", event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserCreatedEvent event) throws JsonProcessingException {
    send("discodeit.UserCreatedEvent", event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserUpdatedEvent event) throws JsonProcessingException {
    send("discodeit.UserUpdatedEvent", event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserDeletedEvent event) throws JsonProcessingException {
    send("discodeit.UserDeletedEvent", event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelCreatedEvent event) throws JsonProcessingException {
    send("discodeit.ChannelCreatedEvent", event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelUpdatedEvent event) throws JsonProcessingException {
    send("discodeit.ChannelUpdatedEvent", event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelDeletedEvent event) throws JsonProcessingException {
    send("discodeit.ChannelDeletedEvent", event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(BinaryContentUpdatedEvent event) throws JsonProcessingException {
    send("discodeit.BinaryContentUpdatedEvent", event);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) throws JsonProcessingException {
    send("discodeit.S3UploadFailedEvent", event);
  }

  private void send(String topic, Object event) throws JsonProcessingException {
    kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
  }
}

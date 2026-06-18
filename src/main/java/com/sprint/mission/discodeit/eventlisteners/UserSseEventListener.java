package com.sprint.mission.discodeit.eventlisteners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.events.UserCreatedEvent;
import com.sprint.mission.discodeit.events.UserDeletedEvent;
import com.sprint.mission.discodeit.events.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSseEventListener {

  private final ObjectMapper objectMapper;
  private final SseService sseService;

  @KafkaListener(
      topics = "discodeit.UserCreatedEvent",
      groupId = "realtime-sse-${discodeit.kafka.realtime-group-id}"
  )
  public void onUserCreated(String kafkaEvent) throws JsonProcessingException {
    UserCreatedEvent event = objectMapper.readValue(kafkaEvent, UserCreatedEvent.class);
    sseService.broadcast("users.created", event.user());
  }

  @KafkaListener(
      topics = "discodeit.UserUpdatedEvent",
      groupId = "realtime-sse-${discodeit.kafka.realtime-group-id}"
  )
  public void onUserUpdated(String kafkaEvent) throws JsonProcessingException {
    UserUpdatedEvent event = objectMapper.readValue(kafkaEvent, UserUpdatedEvent.class);
    sseService.broadcast("users.updated", event.user());
  }

  @KafkaListener(
      topics = "discodeit.UserDeletedEvent",
      groupId = "realtime-sse-${discodeit.kafka.realtime-group-id}"
  )
  public void onUserDeleted(String kafkaEvent) throws JsonProcessingException {
    UserDeletedEvent event = objectMapper.readValue(kafkaEvent, UserDeletedEvent.class);
    sseService.broadcast("users.deleted", event.user());
  }
}

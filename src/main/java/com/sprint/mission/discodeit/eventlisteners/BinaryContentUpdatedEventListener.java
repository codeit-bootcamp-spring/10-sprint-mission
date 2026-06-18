package com.sprint.mission.discodeit.eventlisteners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.events.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentUpdatedEventListener {

  private static final String EVENT_NAME = "binaryContents.updated";

  private final ObjectMapper objectMapper;
  private final SseService sseService;

  @KafkaListener(
      topics = "discodeit.BinaryContentUpdatedEvent",
      groupId = "realtime-sse-${discodeit.kafka.realtime-group-id}"
  )
  public void onBinaryContentUpdated(String kafkaEvent) throws JsonProcessingException {
    BinaryContentUpdatedEvent event = objectMapper.readValue(
        kafkaEvent,
        BinaryContentUpdatedEvent.class
    );
    sseService.broadcast(EVENT_NAME, event.binaryContent());
  }
}

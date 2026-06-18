package com.sprint.mission.discodeit.eventlisteners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.events.ChannelCreatedEvent;
import com.sprint.mission.discodeit.events.ChannelDeletedEvent;
import com.sprint.mission.discodeit.events.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelSseEventListener {

  private final ObjectMapper objectMapper;
  private final SseService sseService;

  @KafkaListener(
      topics = "discodeit.ChannelCreatedEvent",
      groupId = "realtime-sse-${discodeit.kafka.realtime-group-id}"
  )
  public void onChannelCreated(String kafkaEvent) throws JsonProcessingException {
    ChannelCreatedEvent event = objectMapper.readValue(kafkaEvent, ChannelCreatedEvent.class);
    sseService.broadcast("channels.created", event.channel());
  }

  @KafkaListener(
      topics = "discodeit.ChannelUpdatedEvent",
      groupId = "realtime-sse-${discodeit.kafka.realtime-group-id}"
  )
  public void onChannelUpdated(String kafkaEvent) throws JsonProcessingException {
    ChannelUpdatedEvent event = objectMapper.readValue(kafkaEvent, ChannelUpdatedEvent.class);
    sseService.broadcast("channels.updated", event.channel());
  }

  @KafkaListener(
      topics = "discodeit.ChannelDeletedEvent",
      groupId = "realtime-sse-${discodeit.kafka.realtime-group-id}"
  )
  public void onChannelDeleted(String kafkaEvent) throws JsonProcessingException {
    ChannelDeletedEvent event = objectMapper.readValue(kafkaEvent, ChannelDeletedEvent.class);
    sseService.broadcast("channels.deleted", event.channel());
  }
}

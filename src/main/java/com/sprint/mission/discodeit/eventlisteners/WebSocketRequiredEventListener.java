package com.sprint.mission.discodeit.eventlisteners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.events.MessageCreatedEvent;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "realtime-websocket-${discodeit.kafka.realtime-group-id}"
  )
  public void handleMessage(String kafkaEvent) throws JsonProcessingException {
    MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
    MessageDto message = messageService.find(event.messageId());

    messagingTemplate.convertAndSend(
        "/sub/channels." + message.channelId() + ".messages",
        message
    );
  }
}

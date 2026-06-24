package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.UserEvent;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 분산 환경에서 WebSocket/SSE 이벤트를 모든 인스턴스에 전달하기 위한 Kafka 컨슈머.
 * broadcastKafkaListenerContainerFactory를 사용해 인스턴스마다 고유한 group ID로 동작하므로
 * 모든 인스턴스가 각 이벤트를 수신한다(fan-out). 자신에게 연결된 클라이언트에만 실제로 전송된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BroadcastTopicListener {

  private static final String FACTORY = "broadcastKafkaListenerContainerFactory";

  private final SimpMessagingTemplate messagingTemplate;
  private final SseService sseService;
  private final MessageService messageService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", containerFactory = FACTORY)
  public void onMessageCreatedEvent(String payload) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
      MessageDto message = messageService.find(event.messageId());
      messagingTemplate.convertAndSend(
          "/sub/channels." + event.channelId() + ".messages",
          message
      );
      log.debug("WebSocket 전송: channelId={}", event.channelId());
    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent 역직렬화 실패", e);
    }
  }

  @KafkaListener(topics = "discodeit.ChannelEvent", containerFactory = FACTORY)
  public void onChannelEvent(String payload) {
    try {
      ChannelEvent event = objectMapper.readValue(payload, ChannelEvent.class);
      if (event.channel().type() == ChannelType.PRIVATE) {
        List<UUID> participantIds = event.channel().participants().stream()
            .map(com.sprint.mission.discodeit.dto.data.UserDto::id)
            .toList();

        sseService.send(participantIds, event.eventName(), event.channel());
      } else {
        sseService.broadcast(event.eventName(), event.channel());
      }
      log.debug("SSE 전송: {}", event.eventName());
    } catch (JsonProcessingException e) {
      log.error("ChannelEvent 역직렬화 실패", e);
    }
  }

  @KafkaListener(topics = "discodeit.UserEvent", containerFactory = FACTORY)
  public void onUserEvent(String payload) {
    try {
      UserEvent event = objectMapper.readValue(payload, UserEvent.class);
      sseService.broadcast(event.eventName(), event.user());
      log.debug("SSE 전송: {}", event.eventName());
    } catch (JsonProcessingException e) {
      log.error("UserEvent 역직렬화 실패", e);
    }
  }

  @KafkaListener(topics = "discodeit.NotificationCreatedEvent", containerFactory = FACTORY)
  public void onNotificationCreatedEvent(String payload) {
    try {
      NotificationCreatedEvent event = objectMapper.readValue(payload,
          NotificationCreatedEvent.class);
      sseService.send(List.of(event.receiverId()), "notifications.created", event.notification());
      log.debug("SSE 전송: notifications.created → receiverId={}", event.receiverId());
    } catch (JsonProcessingException e) {
      log.error("NotificationCreatedEvent 역직렬화 실패", e);
    }
  }

  @KafkaListener(topics = "discodeit.BinaryContentStatusUpdatedEvent", containerFactory = FACTORY)
  public void onBinaryContentStatusUpdatedEvent(String payload) {
    try {
      BinaryContentStatusUpdatedEvent event = objectMapper.readValue(payload,
          BinaryContentStatusUpdatedEvent.class);
      sseService.broadcast("binaryContents.updated", event.binaryContent());
      log.debug("SSE 전송: binaryContents.updated");
    } catch (JsonProcessingException e) {
      log.error("BinaryContentStatusUpdatedEvent 역직렬화 실패", e);
    }
  }
}

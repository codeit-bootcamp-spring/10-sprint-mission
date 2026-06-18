package com.sprint.mission.discodeit.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.event.kafka.LocalMessageBroadcastEvent;
import com.sprint.mission.discodeit.event.sse.BinaryContentUpdateEvent;
import com.sprint.mission.discodeit.event.sse.ChannelChangedEvent;
import com.sprint.mission.discodeit.event.sse.ChannelChangedEvent.ChannelAction;
import com.sprint.mission.discodeit.event.sse.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.sse.UserChangedEvent;
import com.sprint.mission.discodeit.event.sse.UserChangedEvent.UserAction;
import com.sprint.mission.discodeit.event.sse.UserLogInOutEvent;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTopicListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;
  private final NotificationMapper notificationMapper;
  private final ApplicationEventPublisher eventPublisher;

  @KafkaListener(
      topics = "discodeit.NotificationBroadcast", // 변경된 토픽 수신
      groupId = "#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onNotificationBroadcast(String payload) {
    try {
      List<NotificationDto> notifications = objectMapper.readValue(
          payload,
          new TypeReference<>() {}
      );
      eventPublisher.publishEvent(new NotificationCreatedEvent(notifications));

      log.debug("[KAFKA_CONSUMER] 로컬 SseEventListener로 NotificationCreatedEvent 위임 완료");

    } catch (JsonProcessingException e) {
      log.error("[KAFKA_CONSUMER] 브로드캐스트 역직렬화 실패", e);
    }
  }

  @KafkaListener(
      topics = "discodeit.sse.channel",
      groupId = "#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onChannelBroadcast(String payload) {
    try {
      Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<>() {});
      String action = (String) data.get("action");
      ChannelDto channelDto = objectMapper.convertValue(data.get("channelDto"), ChannelDto.class);

      eventPublisher.publishEvent(new ChannelChangedEvent(channelDto, ChannelAction.valueOf(action)));

    } catch (Exception e) {
      log.error("[KAFKA_CONSUMER] 채널 브로드캐스트 역직렬화 실패", e);
    }
  }

  @KafkaListener(
      topics = "discodeit.sse.user",
      groupId = "#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onUserBroadcast(String payload) {
    try {
      Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<>() {});
      String action = (String) data.get("action");
      UserDto userDto = objectMapper.convertValue(data.get("userDto"), UserDto.class);

      eventPublisher.publishEvent(new UserChangedEvent(userDto, UserAction.valueOf(action)));

    } catch (Exception e) {
      log.error("[KAFKA_CONSUMER] 유저 브로드캐스트 역직렬화 실패", e);
    }
  }

  @KafkaListener(
      topics = "discodeit.sse.userlog",
      groupId = "#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onUserLoginOutBroadcast(String payload) {
    try {
      Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<>() {});
      String userId = (String) data.get("userId");
      boolean isOnline = (boolean) data.get("isOnline");

      eventPublisher.publishEvent(new UserLogInOutEvent(UUID.fromString(userId), isOnline));

    } catch (Exception e) {
      log.error("[KAFKA_CONSUMER] 유저 로그인/로그아웃 브로드캐스트 역직렬화 실패", e);
    }
  }

  @KafkaListener(
      topics = "discodeit.sse.binary",
      groupId = "#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onBinaryContentUpdateBroadcast(String payload) {
    try {
      Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<>() {});
      BinaryContentDto binaryContentDto = objectMapper.convertValue(data.get("dto"), BinaryContentDto.class);
      List<String> receiverIdStrings = (List<String>) data.get("receiverIds");
      List<UUID> receiverIds = receiverIdStrings.stream()
          .map(UUID::fromString)
          .toList();
      eventPublisher.publishEvent(new BinaryContentUpdateEvent(binaryContentDto, receiverIds));

    } catch (Exception e) {
      log.error("[KAFKA_CONSUMER] 파일 상태 업데이트 브로드캐스트 역직렬화 실패", e);
    }
  }

  @KafkaListener(
      topics = "discodeit.ws.message",
      groupId = "#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void onMessageBroadcast(String payload) {
    try {
      MessageDto messageDto = objectMapper.readValue(payload, MessageDto.class);

      eventPublisher.publishEvent(new LocalMessageBroadcastEvent(messageDto));

    } catch (Exception e) {
      log.error("[KAFKA_CONSUMER] 웹소켓 메시지 역직렬화 실패", e);
    }
  }
}

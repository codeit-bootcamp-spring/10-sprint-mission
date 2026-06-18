package com.sprint.mission.discodeit.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  public void broadcastNotifications(List<NotificationDto> notifications) {
    try {
      kafkaTemplate.send("discodeit.NotificationBroadcast",
          objectMapper.writeValueAsString(notifications));
      log.debug("[KAFKA_PRODUCER] 알림 DTO 브로드캐스트 발송 완료");
    } catch (JsonProcessingException e) {
      log.error("[KAFKA_PRODUCER] JSON 직렬화 실패", e);
    }
  }

  @Async("eventTaskExecutor")
  public void broadcastChannelChange(ChannelDto channelDto, String action) {
    try {
      Map<String, Object> payloadMap = Map.of(
          "action", action,
          "channelDto", channelDto
      );
      kafkaTemplate.send("discodeit.sse.channel", objectMapper.writeValueAsString(payloadMap));
    } catch (Exception e) {
      log.error("[KAFKA_PRODUCER] 채널 변경 브로드캐스트 실패", e);
    }
  }

  @Async("eventTaskExecutor")
  public void broadcastUserChange(UserDto userDto, String action) {
    try {
      Map<String, Object> payloadMap = Map.of(
          "action", action,
          "userDto", userDto
      );
      kafkaTemplate.send("discodeit.sse.user", objectMapper.writeValueAsString(payloadMap));
    } catch (Exception e) {
      log.error("[KAFKA_PRODUCER] 유저 변경 브로드캐스트 실패", e);
    }
  }

  @Async("eventTaskExecutor")
  public void broadcastUserLogInOut(UUID userId, boolean isOnline) {
    try {
      Map<String, Object> payload = Map.of("userId", userId, "isOnline", isOnline);
      kafkaTemplate.send("discodeit.sse.userlog", objectMapper.writeValueAsString(payload));
    } catch (Exception e) { log.error("Kafka 발행 실패", e); }
  }

  @Async("eventTaskExecutor")
  public void broadcastBinaryContentUpdate(BinaryContentDto dto, Collection<UUID> receiverIds) {
    try {
      Map<String, Object> payload = Map.of("dto", dto, "receiverIds", receiverIds);
      kafkaTemplate.send("discodeit.sse.binary", objectMapper.writeValueAsString(payload));
    } catch (Exception e) { log.error("Kafka 발행 실패", e); }
  }

  @Async("eventTaskExecutor")
  public void broadcastMessage(MessageDto messageDto) {
    try {
      kafkaTemplate.send("discodeit.ws.message", objectMapper.writeValueAsString(messageDto));
      log.debug("[KAFKA_PRODUCER] 웹소켓 메시지 브로드캐스트 발송 완료");
    } catch (JsonProcessingException e) {
      log.error("[KAFKA_PRODUCER] 웹소켓 메시지 직렬화 실패", e);
    }
  }
}

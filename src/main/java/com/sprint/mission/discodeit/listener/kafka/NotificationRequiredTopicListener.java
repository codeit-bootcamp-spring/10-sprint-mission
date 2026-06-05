package com.sprint.mission.discodeit.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredTopicListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;

  @CacheEvict(value = "notifications", allEntries = true)
  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  @Transactional
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      log.debug("[KAFKA_CONSUMER] 메시지 생성 이벤트 수신: messageId={}", event.messageId());
      String title = String.format("%s (#%s)", event.authorName(), event.channelName());
      String content = event.messageContent();
      List<Notification> notifications = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
              event.channelId())
          .stream()
          .filter(rs -> !rs.getUser().getId().equals(event.authorId()))
          .map(rs -> new Notification(
              rs.getUser(),
              title,
              content
          ))
          .toList();
      notificationRepository.saveAll(notifications);
    } catch (JsonProcessingException e) {
      log.error("[KAFKA_CONSUMER] MessageCreatedEvent 역직렬화 실패", e);
      throw new RuntimeException(e);
    }
  }

  @CacheEvict(value = "notifications", allEntries = true)
  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  @Transactional
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      log.debug("[KAFKA_CONSUMER] 권한 변경 이벤트 수신: userId={}", event.userId());
      String title = "권한이 변경되었습니다.";
      String content = String.format("%s -> %s", event.beforeRole(), event.afterRole());
      User user = userRepository.findById(event.userId())
          .orElseThrow(() -> new UserNotFoundException(Map.of("userId", event.userId())));
      Notification notification = new Notification(
          user,
          title,
          content
      );
      notificationRepository.save(notification);
    } catch (JsonProcessingException e) {
      log.error("[KAFKA_CONSUMER] MessageCreatedEvent 역직렬화 실패", e);
      throw new RuntimeException(e);
    }
  }

  @CacheEvict(value = "notifications", allEntries = true)
  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  @Transactional
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      log.debug("[KAFKA_CONSUMER] S3 파일 업로드 실패 이벤트 수신: binaryContentId={}",
          event.binaryContentId());
      String title = "S3 파일 업로드 실패";
      String content = String.format("RequestId: %s %nBinaryContentId: %s%n Error: %s",
          event.requestId(), event.binaryContentId(), event.errorMessage());
      List<Notification> notifications = userRepository.findAllByRole(Role.ADMIN)
          .stream()
          .map(user -> new Notification(
              user,
              title,
              content
          ))
          .toList();
      notificationRepository.saveAll(notifications);

    } catch (JsonProcessingException e) {
      log.error("[KAFKA_CONSUMER] RoleUpdatedEvent 역직렬화 실패", e);
      throw new RuntimeException(e);
    }
  }
}

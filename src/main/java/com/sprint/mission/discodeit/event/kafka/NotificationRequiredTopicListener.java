package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.kafka.payload.S3UploadFailedKafkaPayload;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;

  @Value("${discodeit.admin.username:admin}")
  private String adminUsername;

  // Kafka topic을 구독해 메시지 생성 알림 생성
  // Kafka 메시지는 중복 소비될 수 있음 -> eventId 기반 멱등 처리로 보완 가능
  @KafkaListener(topics = KafkaTopics.MESSAGE_CREATED)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      log.info("Kafka 메시지 생성 이벤트 수신: messageId={}, channelId={}",
          event.messageId(), event.channelId());

      List<ReadStatus> targetStatuses = readStatusRepository.findAllByChannelIdWithUser(
              event.channelId()).stream()
          .filter(ReadStatus::isNotificationEnabled)
          .filter(rs -> !rs.getUser().getId().equals(event.authorId()))
          .toList();

      String displayChannelName = event.channelName() != null ? event.channelName() : "개인 메시지";
      String title = event.authorName() + " (#" + displayChannelName + ")";

      List<Notification> notifications = targetStatuses.stream()
          .map(rs -> new Notification(
              rs.getUser(),
              title,
              event.content()
          ))
          .toList();

      notificationRepository.saveAll(notifications);

      // Redis/Caffeine 캐시 사용 시 새 알림 생성 후 사용자별 알림 캐시를 무효화
      targetStatuses.forEach(rs -> evictNotificationCache(rs.getUser().getId()));

      log.info("Kafka 메시지 생성 알림 저장 완료: count={}", notifications.size());
    } catch (JsonProcessingException e) {
      log.error("MessageCreatedEvent 역직렬화 실패: payload={}", kafkaEvent, e);
      throw new IllegalArgumentException("MessageCreatedEvent 역직렬화 실패", e);
    }
  }

  // Kafka topic을 구독해 권한 변경 알림 생성
  @KafkaListener(topics = KafkaTopics.ROLE_UPDATED)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      log.info("Kafka 권한 변경 이벤트 수신: userId={}", event.userId());

      User targetUser = userRepository.findById(event.userId())
          .orElseThrow(() -> new IllegalStateException(
              "알림 수신자를 찾을 수 없음: ID = " + event.userId()));

      Notification notification = new Notification(
          targetUser,
          "권한이 변경되었습니다.",
          event.oldRole().name() + " -> " + event.newRole().name()
      );

      notificationRepository.save(notification);
      evictNotificationCache(targetUser.getId());

      log.info("Kafka 권한 변경 알림 저장 완료: userId={}", targetUser.getId());
    } catch (JsonProcessingException e) {
      log.error("RoleUpdatedEvent 역직렬화 실패: payload={}", kafkaEvent, e);
      throw new IllegalArgumentException("RoleUpdatedEvent 역직렬화 실패", e);
    }
  }

  // Kafka topic을 구독해 S3 업로드 실패 관리자 알림 생성
  @KafkaListener(topics = KafkaTopics.S3_UPLOAD_FAILED)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedKafkaPayload event = objectMapper.readValue(
          kafkaEvent,
          S3UploadFailedKafkaPayload.class
      );

      log.info("Kafka S3 업로드 실패 이벤트 수신: binaryContentId={}, requestId={}",
          event.binaryContentId(), event.requestId());

      String title = "S3 파일 업로드 실패";

      String content = """
          RequestId: %s
          BinaryContentId: %s
          ErrorType: %s
          Error: %s"""
          .formatted(
              event.requestId(),
              event.binaryContentId(),
              event.exceptionType(),
              event.errorMessage()
          );

      Optional<User> adminUserOpt = userRepository.findByUsernameWithProfile(adminUsername);

      if (adminUserOpt.isEmpty()) {
        log.warn("관리자 계정을 찾을 수 없어 S3 실패 알림을 생성하지 못함: adminUsername={}", adminUsername);
        return;
      }

      User admin = adminUserOpt.get();

      Notification notification = new Notification(admin, title, content);
      notificationRepository.save(notification);
      evictNotificationCache(admin.getId());

      log.info("Kafka S3 업로드 실패 알림 저장 완료: adminUserId={}", admin.getId());
    } catch (JsonProcessingException e) {
      log.error("S3UploadFailedKafkaPayload 역직렬화 실패: payload={}", kafkaEvent, e);
      throw new IllegalArgumentException("S3UploadFailedKafkaPayload 역직렬화 실패", e);
    }
  }

  private void evictNotificationCache(Object receiverId) {
    Cache cache = cacheManager.getCache("notifications");

    if (cache != null) {
      cache.evict(receiverId);
    }
  }
}

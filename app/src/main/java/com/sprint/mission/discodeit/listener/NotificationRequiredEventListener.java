package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.kafka.KafkaBinaryContentUpdateEvent;
import com.sprint.mission.discodeit.event.kafka.KafkaChannelChangedEvent;
import com.sprint.mission.discodeit.event.kafka.KafkaUserChangedEvent;
import com.sprint.mission.discodeit.event.kafka.KafkaUserLogInOutEvent;
import com.sprint.mission.discodeit.event.sse.ChannelChangedEvent.ChannelAction;
import com.sprint.mission.discodeit.event.sse.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.listener.kafka.KafkaProducer;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final KafkaProducer kafkaProducer;

  @CacheEvict(value = "notifications", allEntries = true)
  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    log.debug("[NOTIFICATION] 메시지 생성 이벤트 수신: messageId={}", event.messageId());
    String title = String.format("%s (#%s)", event.messageDto().author().username(),
        event.channelName());
    String content = event.messageDto().content();
    List<Notification> notifications = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
            event.messageDto().channelId())
        .stream()
        .filter(rs -> !rs.getUser().getId().equals(event.messageDto().author().id()))
        .map(rs -> new Notification(
            rs.getUser(),
            title,
            content
        ))
        .toList();
    if (notifications.isEmpty()) return;
    notificationRepository.saveAll(notifications);
    List<NotificationDto> notificationDtos = notifications.stream()
        .map(notificationMapper::toDto)
        .toList();
    kafkaProducer.broadcastNotifications(notificationDtos);
  }

  @CacheEvict(value = "notifications", allEntries = true)
  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    log.debug("[NOTIFICATION] 권한 변경 이벤트 수신: userId={}", event.userId());
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
    NotificationDto dto = notificationMapper.toDto(notification);
    kafkaProducer.broadcastNotifications(List.of(dto));
  }

  @CacheEvict(value = "notifications", allEntries = true)
  @Async("eventTaskExecutor")
  @EventListener
  @Transactional
  public void on(S3UploadFailedEvent event) {
    log.debug("[NOTIFICATION] S3 파일 업로드 실패 이벤트 수신: binaryContentId={}", event.binaryContentId());
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
    List<NotificationDto> notificationDtos = notifications.stream()
        .map(notificationMapper::toDto)
        .toList();
    kafkaProducer.broadcastNotifications(notificationDtos);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(KafkaChannelChangedEvent event) {
    log.debug("[KAFKA] kafka 채널 갱신 이벤트 수신: channelId={}", event.channelDto().id());
    kafkaProducer.broadcastChannelChange(event.channelDto(), event.action());
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(KafkaUserChangedEvent event) {
    log.debug("[KAFKA] kafka 유저 갱신 이벤트 수신: userId={}", event.userDto().id());
    kafkaProducer.broadcastUserChange(event.userDto(), event.action());
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(KafkaUserLogInOutEvent event) {
    log.debug("[KAFKA] kafka 유저 갱신 이벤트 수신: userId={}", event.userId());
    kafkaProducer.broadcastUserLogInOut(event.userId(), event.isOnline());
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(KafkaBinaryContentUpdateEvent event) {
    log.debug("[KAFKA] kafka 파일 갱신 이벤트 수신: binaryContentId={}", event.binaryContentDto().id());
    kafkaProducer.broadcastBinaryContentUpdate(event.binaryContentDto(), event.receiverIds());
  }
}

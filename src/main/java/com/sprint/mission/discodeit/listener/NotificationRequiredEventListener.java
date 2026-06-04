package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Async("asyncExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    log.debug("[NOTIFICATION] 메시지 생성 이벤트 수신: messageId={}", event.messageId());
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
  }

  @Async("asyncExecutor")
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
  }

  @Async("asyncExecutor")
  @EventListener
  @Transactional
  public void handleBinaryContentUploadFailedEvent(BinaryContentUploadFailedEvent event) {
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
  }
}

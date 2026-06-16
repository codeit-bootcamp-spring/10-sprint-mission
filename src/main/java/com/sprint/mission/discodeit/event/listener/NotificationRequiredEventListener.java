package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Value("${discodeit.admin.username:admin}")
  private String adminUsername;

  // 메시지 전송 트랜잭션 커밋 직후 실행
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  // 알림 생성을 DB에 반영하려면 새 트랜잭션을 열어야 함
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    log.info("메시지 생성 이벤트 수신: 채널 ID = {}", event.channelId());

    // 1. 해당 채널의 ReadStatus 조회 (알림 켜져 있는 사람만 + 작성자 제외)
    List<ReadStatus> targetStatuses = readStatusRepository.findAllByChannelIdWithUser(
            event.channelId()).stream()
        .filter(ReadStatus::isNotificationEnabled)
        .filter(rs -> !rs.getUser().getId().equals(event.authorId()))
        .toList();

    // 2. 알림 엔티티 생성 및 벌크 저장
    String displayChannelName = event.channelName() != null ? event.channelName() : "개인 메시지";
    String title = event.authorName() + " (#" + displayChannelName + ")";

    List<Notification> notifications = targetStatuses.stream()
        .map(rs -> new Notification(
            rs.getUser(),
            title,
            event.content()
        )).toList();

    notificationRepository.saveAll(notifications);
    log.info("총 {}건의 메시지 알림 생성 완료", notifications.size());
  }

  // 권한 변경 트랜잭션 커밋 직후 실행
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  // 알림 생성을 DB에 반영하려면 새 트랜잭션을 열어야 함
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    log.info("권한 변경 이벤트 수신: 대상 ID = {}", event.userId());

    // 저장 전 대상 User 엔티티를 영속성 컨텍스트로 불러옴
    User targetUser = userRepository.findById(event.userId())
        .orElseThrow(() -> new IllegalStateException("알림 수신자를 찾을 수 없음: ID = " + event.userId()));

    Notification notification = new Notification(
        targetUser,
        "권한이 변경되었습니다.",
        event.oldRole().name() + " -> " + event.newRole().name()
    );

    notificationRepository.save(notification);
  }

  @EventListener
  public void on(S3UploadFailedEvent event) {
    String requestId = event.requestId();
    UUID binaryContentId = event.binaryContentId();
    Throwable e = event.exception();

    String title = "S3 파일 업로드 실패";

    String content = """
        RequestId: %s
        BinaryContentId: %s
        Error: %s"""
        .formatted(requestId, binaryContentId, e.getMessage());

    // 관리자 계정 조회
    Optional<User> adminUserOpt = userRepository.findByUsernameWithProfile(adminUsername);

    // 관리자 계정에 알림 생성
    if (adminUserOpt.isPresent()) {
      User admin = adminUserOpt.get();

      Notification notification = new Notification(admin, title, content);
      notificationRepository.save(notification);
    }
  }
}

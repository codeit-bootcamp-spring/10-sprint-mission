package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

// @Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    String channelDisplay = event.channelName() != null
        ? "#" + event.channelName() : "비공개 채널";
    String title = event.authorUsername() + " (#" + channelDisplay + ")";

    readStatusRepository
        // 해당 채널에서 notificationEnabled=true인 ReadStatus 목록 조회
        .findAllByChannelIdAndNotificationEnabled(event.channelId())
        .stream()
        // 작성자 본인 제외
        .filter(rs -> !rs.getUser().getId().equals(event.authorId()))
        // 나머지 유저에 대해 notificationService.create() 호출
        .forEach(rs -> notificationService.create(rs.getUser().getId(), title, event.content()));
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    String content = event.oldRole().name() + " -> " + event.newRole().name();
    notificationService.create(event.userId(), "권한이 변경되었습니다.", content);
  }


  // S3 파일 업로드 최종 실패 이벤트를 수신해서 관리자에게 알림을 전송
  @Async("eventTaskExecutor")
  @EventListener // 트랜잭션 없이 즉시 실행 - 업로드 실패 알림은 트랜잭션 결과와 무관하게 즉시 전달되어야 하므로 사용
  public void on(S3UploadFailedEvent event) {
    String requestId = event.getRequestId();
    UUID binaryContentId = event.getBinaryContentId();
    Throwable e = event.getE();
    String reason = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();

    String title = "[관리자 알림] S3 파일 업로드 실패";
    String content = String.format(
        "작업: %s%nRequestId: %s%nBinaryContentId: %s%nError: %s",
        "S3 파일 업로드", requestId, binaryContentId, reason);

    // 관리자에게 알림을 생성
    userRepository.findAllByRole(UserRole.ADMIN)
        .forEach(admin -> notificationService.create(admin.getId(), title, content));
  }
}

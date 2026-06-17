package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.sse.BinaryContentUpdateEvent;
import com.sprint.mission.discodeit.event.sse.ChannelChangedEvent;
import com.sprint.mission.discodeit.event.sse.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.sse.UserChangedEvent;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
public class SseEventListener {

  private final SseService sseService;

  @Async("eventTaskExecutor")
  @EventListener
  public void on(NotificationCreatedEvent event) {
    log.debug("[SSE] 알림 생성 이벤트 수신: size={}", event.notificationDtos().size());
    for (NotificationDto notificationDto : event.notificationDtos()) {
      sseService.send(
          List.of(notificationDto.receiverId()),
          "notifications.created",
          notificationDto
      );
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    log.info("[SSE] 권한 변경 감지, 유저 SSE 연결 강제 종료 userId={}", event.userId());
    sseService.disconnect(event.userId());
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(BinaryContentUpdateEvent event) {
    log.debug("[SSE] 파일 업로드 상태 변경 이벤트 수신: binaryContentId={}", event.binaryContentDto().id());
    if (event.receiverIds() == null || event.receiverIds().isEmpty()) {
      log.debug("[SSE] 수신자가 지정되지 않아 이벤트 폐기 (예: 회원가입 프로필 업로드)");
      return;
    }
    sseService.send(
        event.receiverIds(),
        "binaryContents.updated",
        event.binaryContentDto()
    );
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelChangedEvent event) {
    log.debug("[SSE] 채널 갱신 이벤트 수신: channelId={}", event.channelDto().id());
    sseService.broadcast(
        "channels." + event.action().name().toLowerCase(),
        event.channelDto()
    );
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserChangedEvent event) {
    log.debug("[SSE] 사용자 갱신 이벤트 수신: channelId={}", event.userDto().id());
    sseService.broadcast(
        "users." + event.action().name().toLowerCase(),
        event.userDto()
    );
  }
}

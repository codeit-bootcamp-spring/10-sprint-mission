package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.message.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.message.NotificationCreatedEvent;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(NotificationCreatedEvent event) {
    NotificationDto notification = event.getData();

    sseService.send(Set.of(notification.receiverId()), "notifications.created", notification);

    log.debug(
        "SSE 알림 생성 이벤트 전송 완료: notificationId={}, receiverId={}",
        notification.id(),
        notification.receiverId());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(BinaryContentUpdatedEvent event) {
    BinaryContentDto binaryContent = event.getTo();

    sseService.broadcast("binaryContents.updated", binaryContent);

    log.debug(
        "SSE 파일 상태 변경 이벤트 전송 완료: binaryContentId={}, status={}",
        binaryContent.id(),
        binaryContent.status());
  }
}

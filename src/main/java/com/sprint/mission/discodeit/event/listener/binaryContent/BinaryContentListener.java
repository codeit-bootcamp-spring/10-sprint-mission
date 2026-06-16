package com.sprint.mission.discodeit.event.listener.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentEvents;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 바이너리 컨텐츠 생성 이벤트를 구독하여 실제 스토리지 업로드를 처리하는 리스너입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async("ioTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreated(BinaryContentEvents.Created event) {
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("[BinaryContentListener] 업로드 성공: ID={}", event.binaryContentId());
    } catch (Exception e) {
      log.error("[BinaryContentListener] 업로드 실패: ID={}, Error={}", event.binaryContentId(), e.getMessage());
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
    }
  }
}

package com.sprint.mission.discodeit.util.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinaryContentListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async("binaryContentTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreateEvent(BinaryContentCreatedEvent event)
      throws InterruptedException {
    Thread.sleep(3000);
    try {
      binaryContentStorage.put(event.binaryContent().getId(), event.bytes());
      log.debug("바이너리 컨텐츠 스토리지 저장 성공");
      binaryContentService.updateStatus(event.binaryContent().getId(), BinaryContentStatus.SUCCESS);
      log.debug("바이너리 컨텐츠 스토리지 상태 변경 성공");
    } catch (Exception e) {
      log.error(e.getMessage());
      binaryContentService.updateStatus(event.binaryContent().getId(), BinaryContentStatus.FAIL);
      log.debug("바이너리 컨텐츠 스토리지 상태 변경 성공");
      throw e;
    }

  }
}

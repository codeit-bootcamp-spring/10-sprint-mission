package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    log.debug("바이너리 콘텐츠 생성 이벤트 처리 시작: id={}", event.binaryContentId());
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("바이너리 콘텐츠 생성 이벤트 처리 완료 (SUCCESS): id={}", event.binaryContentId());
    } catch (Exception e) {
      log.error("바이너리 콘텐츠 생성 이벤트 처리 실패 (FAIL): id={}", event.binaryContentId(), e);
      try {
        binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
      } catch (Exception ex) {
        log.error("바이너리 콘텐츠 상태를 FAIL로 변경하는 중 오류 발생: id={}", event.binaryContentId(), ex);
      }
    }
  }
}

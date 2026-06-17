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
public class BinaryContentCreatedEventListner {
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    log.debug("바이너리 데이터 저장 시작: binaryContentId={}", event.binaryContentId());

    try {
      // 실제 파일을 S3에 저장합니다.
      binaryContentStorage.put(event.binaryContentId(), event.bytes());

      // 파일 저장이 성공, 메타데이터 상태를 SUCCESS로.
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);

      log.debug("바이너리 데이터 저장 성공: binaryContentId={}", event.binaryContentId());
    } catch (Exception e) {
      // 파일 저장 중 예외 발생시, 실패 상태는 DB로
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);

      log.warn("바이너리 데이터 저장 실패: binaryContentId={}", event.binaryContentId(), e);
    }
  }
}

package com.sprint.mission.discodeit.event.listener;

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
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  @Async("eventExecutor")
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    try{
      binaryContentStorage.put(event.binaryContentId(), event.bytes(), event.fileName(), event.contentType());
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
    }catch (Exception e){
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
      log.error("파일 업로드 실패, 파일 id:{}", event.binaryContentId());
    }
  }

}

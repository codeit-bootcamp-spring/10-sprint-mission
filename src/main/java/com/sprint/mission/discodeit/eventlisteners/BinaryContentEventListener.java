package com.sprint.mission.discodeit.eventlisteners;

import com.sprint.mission.discodeit.events.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.basic.BinaryContentUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@Async("eventTaskExecutor")
public class BinaryContentEventListener {

  private final BinaryContentUploadService binaryContentUploadService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void binaryContentSaved(BinaryContentCreatedEvent event) {
    binaryContentUploadService.upload(event.binaryContentId(), event.bytes());
  }
}

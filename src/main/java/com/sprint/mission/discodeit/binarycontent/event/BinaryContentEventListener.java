package com.sprint.mission.discodeit.binarycontent.event;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent binaryContentCreatedEvent) {
    try {
      binaryContentStorage.put(binaryContentCreatedEvent.binaryContentId(),
          binaryContentCreatedEvent.bytes());

      binaryContentService.updateStatus(binaryContentCreatedEvent.binaryContentId(),
          BinaryContentStatus.SUCCESS);
    } catch (Exception e) {
      binaryContentService.updateStatus(binaryContentCreatedEvent.binaryContentId(),
          BinaryContentStatus.FAIL);
    }
  }

}

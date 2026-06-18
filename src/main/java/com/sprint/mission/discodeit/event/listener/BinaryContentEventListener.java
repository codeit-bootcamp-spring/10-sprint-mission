package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.message.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.sse.SseService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentEventListener {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final SseService sseService;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(BinaryContentCreatedEvent event) {
    BinaryContent binaryContent = event.getData();
    BinaryContentStatus status;
    try {
      binaryContentStorage.put(
          binaryContent.getId(),
          event.getBytes()
      );
      status = BinaryContentStatus.SUCCESS;
    } catch (RuntimeException e) {
      status = BinaryContentStatus.FAIL;
    }
    BinaryContentDto dto = binaryContentService.updateStatus(
        binaryContent.getId(), status
    );
    sseService.broadcast("binaryContents.updated", dto);
  }
}

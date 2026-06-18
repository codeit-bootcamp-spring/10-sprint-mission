package com.sprint.mission.discodeit.util.listener;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
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
  private final SseService sseService;
  private final ReadStatusRepository readStatusRepository;

  @Async("binaryContentTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreateEvent(BinaryContentCreatedEvent event)
      throws InterruptedException {
    Thread.sleep(3000);
    try {
      binaryContentStorage.put(event.binaryContent().getId(), event.bytes());
      log.debug("바이너리 컨텐츠 스토리지 저장 성공");
      BinaryContentDto response = binaryContentService.updateStatus(event.binaryContent().getId(),
          BinaryContentStatus.SUCCESS);
      log.debug("바이너리 컨텐츠 스토리지 상태 변경 성공");
      sendSseEvent(event, response);
    } catch (Exception e) {
      log.error(e.getMessage());
      BinaryContentDto response = binaryContentService.updateStatus(event.binaryContent().getId(),
          BinaryContentStatus.FAIL);
      sendSseEvent(event, response);
      log.debug("바이너리 컨텐츠 스토리지 상태 변경 성공");
      throw e;
    }
  }

  private void sendSseEvent(BinaryContentCreatedEvent event, BinaryContentDto response) {
    if (event.targetChannelId() == null) {
      sseService.broadcast("binaryContents.updated", response);
    } else {
      List<UUID> memberIds = readStatusRepository.findAllByChannelId(event.targetChannelId())
          .stream()
          .map(r -> r.getUser().getId()).toList();
      sseService.send(memberIds, "binaryContents.updated", response);
    }
  }
}

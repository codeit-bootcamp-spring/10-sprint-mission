package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.sse.BinaryContentStatusChangedEvent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3UploadRetryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final S3UploadRetryManager s3UploadRetryManager;
  private final BinaryContentMapper binaryContentMapper;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
        .orElseThrow();
    try {
      s3UploadRetryManager.upload(event.binaryContentId(), event.bytes());
      binaryContent.updateStatus(BinaryContentStatus.SUCCESS);
      log.info("바이너리 컨텐츠 파일 저장 성공: id={}", event.binaryContentId());
    } catch (Exception e) {
      binaryContent.updateStatus(BinaryContentStatus.FAIL);
      log.error("바이너리 컨텐츠 파일 저장 실패: id={}", event.binaryContentId(), e);
    }
    applicationEventPublisher.publishEvent(
        new BinaryContentStatusChangedEvent(binaryContentMapper.toDto(binaryContent)));
  }
}

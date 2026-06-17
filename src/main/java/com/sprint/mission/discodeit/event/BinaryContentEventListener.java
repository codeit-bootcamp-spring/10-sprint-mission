package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3UploadRetryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  /* 현재 실행 중인 트랜잭션이 성공적으로 커밋된 직후에 이벤트를 처리하도록 함
  AFTER_COMMIT을 통해 DB 처리가 확정된 뒤에 파일 관련 IO 작업 진행
  (DB 저장이 롤백될 수 있는 상황에서 파일 업로드 방지)

  <동작 순서>
  1 서비스 메서드 시작 (@Transactional)
  2 DB 저장 binaryContentRepository.save(binaryContent)
  3 publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes))
    Spring이 이벤트 객체를 내부에 보관
  4 트랜잭션 커밋
  5 Spring이 꺼내서 리스너 실행 -> handleBinaryContentCreated(BinaryContentCreatedEvent event) 호출
  6 binaryContentStorage.put(event.binaryContentId(), event.bytes());

  https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html
  https://docs.spring.io/spring-modulith/reference/events.html

  AFTER_COMMIT     : 트랜잭션이 성공적으로 커밋된 후에 이벤트를 실행
  AFTER_ROLLBACK   : 트랜잭션이 롤백된 후에 이벤트를 실행
  AFTER_COMPLETION : 트랜잭션이 성공하든 실패하든 관계없이 완료되면 이벤트를 실행
  BEFORE_COMMIT    : 트랜잭션이 커밋되기 직전에 이벤트를 실행

  같은 스레드, 새 트랜잭션 (블로킹 발생 - TX-1 완료 후 리스너 끝날 때까지 스레드가 블로킹됨)
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)

  다른 스레드, 트랜잭션 없음 (비동기)
  @Async
  @TransactionalEventListener
  ```markdown
  [스레드 A]            [스레드 B (스레드풀)]
  TX-1 시작
  -> 비즈니스 로직
  -> TX-1 커밋
  -> 리스너 호출 -------> 리스너 실행 시작
  -> 즉시 반환           (ThreadLocal 깨끗한 상태)
  -> TX-1 정리           -> 필요시 새 TX 시작 (DB 접근 등)
                         -> 로직 실행
                         -> TX 커밋 or 롤백
   ```
   */
  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // AFTER_COMMIT이 기본값이지만 명시함
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
  }
}

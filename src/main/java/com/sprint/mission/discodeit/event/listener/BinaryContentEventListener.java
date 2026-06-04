package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  // 서비스 메인 트랜잭션이 커밋된 후(DB에 메타데이터가 저장된 후)에만 리스너가 실행되도록 설정
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  // 메인 트랜잭션은 이미 끝났으므로, 상태값 업데이트를 DB에 반영하려면 새 트랜잭션을 열어야 함
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
    log.info("바이너리 업로드 이벤트 수신: 파일 ID = {}", event.binaryContentId());

    try {
      // 리스너에서 실제 스토리지 업로드 수행
      binaryContentStorage.put(event.binaryContentId(), event.bytes());

      // 성공 시 SUCCESS 상태 변경
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("바이너리 업로드 완료 및 상태 변경 SUCCESS: 파일 ID = {}", event.binaryContentId());

    } catch (Exception e) {
      log.error("바이너리 업로드 실패: 파일 ID = {}", event.binaryContentId(), e);
      // 실패 시 FAIL 상태 변경
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
    }
  }
}

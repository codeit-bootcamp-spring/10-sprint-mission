package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
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
public class BinaryContentStorageEventListener {

    private final BinaryContentStorage  binaryContentStorage;
    private final BasicBinaryContentService binaryContentService;

    /// (5)Thread-2: @Async때문에 별도 스레드로 위임 -> s3 업로드 시작.
    /// 이벤트를 발행한 메인 서비스의 트랜잭션이 커밋되었을때 리스너가 실행되도록 설정
    /// BinaryContentCreatedEvent타입 기준으로 매칭
    /// Thread-1은 BinaryContentService.create()를 commit 시키고 Thread-2에게 이 리스너 위임한다.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleBinaryContentStorage(BinaryContentCreatedEvent event) {
        try {
            log.info("프로필 저장ID: {}", event.getBinaryContentId());
            /// BinaryCOntentStorage를 통해 바이너리 데이터를 저장.
            binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());
        } catch (Exception e) {
            log.error("S3 업로드 실패");
            binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.FAIL);
        }

        /// put이 끝나면 실행.
        /// s3 업로드 완료시 status = SUCCESS
        binaryContentService.updateStatus(event.binaryContentId, BinaryContentStatus.SUCCESS);
    }




}

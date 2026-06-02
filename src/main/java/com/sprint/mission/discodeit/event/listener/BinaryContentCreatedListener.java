package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync
public class BinaryContentCreatedListener {
    private final BinaryContentStorage s3BinaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProfileUpload(BinaryContentCreatedEvent event){
        UUID binaryContentId = event.getBinaryContentId();
        try{
            log.info("비동기 s3 업로드 시작: id = {}", binaryContentId);
            s3BinaryContentStorage.put(binaryContentId, event.getBytes());
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
            log.info("비동기 s3 업로드 완료: id = {}", binaryContentId);
        } catch (Exception e) {
            log.error("비동기 s3 업로드 실패: id = {}", binaryContentId);
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
            throw new RuntimeException(e);
        } finally {
            event.clear();
        }
    }
}

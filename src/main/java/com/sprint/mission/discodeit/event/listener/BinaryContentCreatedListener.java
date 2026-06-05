package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinaryContentCreatedListener {
    private final BinaryContentStorage s3BinaryContentStorage;
    private final BinaryContentService binaryContentService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    @Value("${admin.init.email}")
    private String adminEmail;

    @Async("ioTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProfileUpload(BinaryContentCreatedEvent event){
        UUID binaryContentId = event.getBinaryContentId();
        String requestId = MDC.get("requestId");
        try{
            log.info("비동기 s3 업로드 시작: id = {}", binaryContentId);
            s3BinaryContentStorage.put(binaryContentId, event.getBytes());
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
            log.info("비동기 s3 업로드 완료: id = {}", binaryContentId);
        } catch (FileUploadFailException e){
            log.error("비동기 s3 업로드 최종 실패 처리 로직: id = {}, requestId = {}", binaryContentId, requestId);
            // DB 상태 변경
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
            try {
                User admin = userRepository.findByEmail(adminEmail)
                        .orElseThrow(() -> new UserNotFoundException(adminEmail));

                notificationRepository.save(new Notification(
                        admin,
                        "S3 파일 업로드 실패",
                        String.format("RequestId: %s\nBinaryContentId: %s\nError: %s",
                                requestId, binaryContentId, e.getErrorCode().getMessage())
                ));
        } catch (Exception ex) {
                log.error("관리자 알림 전송 실패", ex);
            }
        } finally{
            event.clear();
        }
    }
}

package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.enums.binarycontents.BinaryContentStatus;
import com.sprint.mission.discodeit.events.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.exception.SdkException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BinaryContentUploadService {

  private static final String TASK_NAME = "파일 업로드 실패";

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final ApplicationEventPublisher eventPublisher;

  // 바이너리 컨텐츠를 스토리지에 저장하는 로직.
  // Retryable로 재시도 정책 명시
  @Retryable(
      retryFor = {IOException.class, IllegalStateException.class, SdkException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void upload(UUID binaryContentId, byte[] bytes) {

    binaryContentStorage.put(binaryContentId, bytes); // 스토리지에 바이너리 컨텐츠 저장
    binaryContentService.updateStatus(binaryContentId,
        BinaryContentStatus.SUCCESS); // 바이너리 컨텐츠의 상태를 SUCCESS
  }

  // 장애 시 복구 로직
  @Recover
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recover(Exception exception, UUID binaryContentId, byte[] bytes) {
    // 해당 바이너리 컨텐츠의 상태를 FAIL로 변경
    binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
    eventPublisher.publishEvent(
        new S3UploadFailedEvent(
            binaryContentId
        ));
  }
}

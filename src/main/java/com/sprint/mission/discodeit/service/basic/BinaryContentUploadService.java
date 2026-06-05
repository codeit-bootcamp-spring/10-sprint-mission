package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.enums.binarycontents.BinaryContentStatus;
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
    notifyAdmins(binaryContentId, exception); // 어드민에게 알림 생성 및 전달
  }

  // 어드민(관리자) 에게 전달할 알림을 생성하고 전달
  private void notifyAdmins(UUID binaryContentId, Exception exception) {
    String content = """
        Task: %s
        RequestId: %s
        BinaryContentId: %s
        Error: %s
        """.formatted(
        TASK_NAME,
        MDC.get("requestId"),
        binaryContentId,
        exception.getMessage()
    );

    // 권한이 ADMIN 유저를 리스트 형식으로 뽑고
    List<User> admins = userRepository.findAllByRole(Role.ADMIN);
    // 유저 리스트를 순회하면서 알림 객체 생성 및 저장
    for (User admin : admins) {
      notificationRepository.save(new Notification(
          admin,
          "작업 실패: " + TASK_NAME,
          content
      ));
    }
  }
}

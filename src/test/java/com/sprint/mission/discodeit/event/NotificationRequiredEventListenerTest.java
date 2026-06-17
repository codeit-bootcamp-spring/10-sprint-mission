package com.sprint.mission.discodeit.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.s3.S3UploadException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredEventListenerTest {

  @Mock
  private NotificationService notificationService;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private NotificationRequiredEventListener listener;

  private User admin(String username) {
    BinaryContent profile = new BinaryContent("profile.jpg", 1024L, "image/jpeg");
    User user = new User(
        username, username + "@example.com", "password123!@#", profile, UserRole.ADMIN);
    // id 필드는 엔티티 생성 시 JPA가 채워주지만, 단위 테스트에서는 JPA가 없으므로 ReflectionTestUtils로 직접 주입
    // @GeneratedValue로 생성되는 id를 JPA 없이 세팅: 알림 생성 시 수신자 id 인자로 사용
    ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
    return user;
  }

  @Test
  @DisplayName("S3 업로드 실패 이벤트 수신 시 모든 관리자에게 디버깅 정보를 담은 알림을 생성한다")
  void onS3UploadFailed_notifiesAllAdminsWithDebugInfo() {
    // given
    User admin1 = admin("admin1");
    User admin2 = admin("admin2");
    given(userRepository.findAllByRole(UserRole.ADMIN)).willReturn(List.of(admin1, admin2));

    UUID binaryContentId = UUID.randomUUID();
    S3UploadFailedEvent event = new S3UploadFailedEvent(
        binaryContentId,
        new S3UploadException("S3에 파일 업로드 실패: " + binaryContentId,
            new RuntimeException(
                "The AWS Access Key Id you provided does not exist in our records.")),
        "req-123");

    // when
    listener.on(event);

    // then
    // String 타입의 ArgumentCaptor 생성
    ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
    verify(notificationService, times(2))
        .create(any(UUID.class), anyString(), contentCaptor.capture());

    String content = contentCaptor.getValue();
    assertThat(content)
        .contains("S3 파일 업로드") // 실패한 작업 이름
        .contains("req-123") // MDC Request ID
        .contains(binaryContentId.toString()) // BinaryContentId
        .contains(
            "The AWS Access Key Id you provided does not exist in our records."); // 실제 원인(cause) 메시지
  }

  @Test
  @DisplayName("예외에 cause 가 없으면 래퍼 예외의 메시지를 실패 사유로 사용한다")
  void onS3UploadFailed_noCause_usesWrapperMessage() {
    // given
    User admin1 = admin("admin1");
    given(userRepository.findAllByRole(UserRole.ADMIN)).willReturn(List.of(admin1));

    UUID binaryContentId = UUID.randomUUID();
    S3UploadFailedEvent event = new S3UploadFailedEvent(
        binaryContentId,
        new S3UploadException("원인 없는 업로드 실패", null),
        "req-456");

    // when
    listener.on(event);

    // then
    ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
    verify(notificationService).create(any(UUID.class), anyString(), contentCaptor.capture());
    assertThat(contentCaptor.getValue()).contains("원인 없는 업로드 실패");
  }

  @Test
  @DisplayName("관리자가 없으면 알림을 생성하지 않는다")
  void onS3UploadFailed_noAdmins_doesNotNotify() {
    // given
    given(userRepository.findAllByRole(UserRole.ADMIN)).willReturn(List.of());

    S3UploadFailedEvent event = new S3UploadFailedEvent(
        UUID.randomUUID(),
        new S3UploadException("실패", new RuntimeException("error")),
        "req-789");

    // when
    listener.on(event);

    // then
    verify(notificationService, never()).create(any(), any(), any());
  }
}

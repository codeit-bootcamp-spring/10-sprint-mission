package com.sprint.mission.discodeit.binarycontent.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.event.UploadFailedNotificationEvent;
import com.sprint.mission.discodeit.binarycontent.domain.provider.FileUploadResult;
import com.sprint.mission.discodeit.binarycontent.infra.repository.BinaryContentRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class BinaryContentStorageCallbackTest {

  @Mock
  private BinaryContentRepository repository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private BinaryContentStorageCallback callback;

  @Test
  @DisplayName("handleSuccess - 성공한 파일들의 상태를 SUCCESS로 업데이트한다.")
  void handleSuccess() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    List<FileUploadResult> results = List.of(
        new FileUploadResult(id1, false, null),
        new FileUploadResult(id2, false, null)
    );

    given(repository.updateStatus(List.of(id1, id2), BinaryContentStatus.SUCCESS)).willReturn(2);

    callback.handleSuccess(results);

    verify(repository).updateStatus(List.of(id1, id2), BinaryContentStatus.SUCCESS);
  }

  @Test
  @DisplayName("handleFailures - 실패한 파일들의 상태를 FAILED로 업데이트하고 이벤트를 발행한다.")
  void handleFailures() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    List<FileUploadResult> results = List.of(
        new FileUploadResult(id1, true, "error1"),
        new FileUploadResult(id2, true, "error2")
    );

    given(repository.updateStatus(any(), eq(BinaryContentStatus.FAILED))).willReturn(2);

    callback.handleFailures(results);

    verify(repository).updateStatus(any(), eq(BinaryContentStatus.FAILED));
    
    ArgumentCaptor<UploadFailedNotificationEvent> captor = ArgumentCaptor.forClass(UploadFailedNotificationEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    
    UploadFailedNotificationEvent event = captor.getValue();
    assertThat(event.title()).isEqualTo("file upload failed");
    assertThat(event.messages()).hasSize(2);
  }
}

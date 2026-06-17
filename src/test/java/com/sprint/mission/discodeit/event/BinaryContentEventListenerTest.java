package com.sprint.mission.discodeit.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.s3.S3UploadException;
import com.sprint.mission.discodeit.storage.s3.S3UploadRetryManager;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryContentEventListenerTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private S3UploadRetryManager s3UploadRetryManager;

  @InjectMocks
  private BinaryContentEventListener listener;

  @Test
  @DisplayName("업로드가 최종 실패하면 BinaryContent 상태를 FAIL 로 기록한다")
  void handleBinaryContentCreated_uploadFails_marksFail() {
    // given
    UUID id = UUID.randomUUID();
    byte[] bytes = {1, 2, 3};
    BinaryContent binaryContent = new BinaryContent("file.png", 3L, "image/png");

    given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));
    given(s3UploadRetryManager.upload(any(), any()))
        .willThrow(new S3UploadException("실패", new RuntimeException("error")));

    // when
    listener.handleBinaryContentCreated(new BinaryContentCreatedEvent(id, bytes));

    // then
    assertThat(binaryContent.getStatus()).isEqualTo(BinaryContentStatus.FAIL);
  }

  @Test
  @DisplayName("업로드가 성공하면 BinaryContent 상태를 SUCCESS 로 기록한다")
  void handleBinaryContentCreated_uploadSucceeds_marksSuccess() {
    // given
    UUID id = UUID.randomUUID();
    byte[] bytes = {1, 2, 3};
    BinaryContent binaryContent = new BinaryContent("file.png", 3L, "image/png");

    given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));
    given(s3UploadRetryManager.upload(any(), any())).willReturn(id);

    // when
    listener.handleBinaryContentCreated(new BinaryContentCreatedEvent(id, bytes));

    // then
    assertThat(binaryContent.getStatus()).isEqualTo(BinaryContentStatus.SUCCESS);
  }
}

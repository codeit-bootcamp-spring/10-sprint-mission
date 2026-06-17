package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
@Retryable / @Recover는 Spring AOP 프록시 위에서만 동작한다.
@RecordApplicationEvents: 테스트 컨텍스트 내에서 발행된 모든 ApplicationEvent를 ApplicationEvents에 기록
 */
@ExtendWith(SpringExtension.class) // JUnit 5 에서 Spring 테스트 컨텍스트를 활성화
@ContextConfiguration(classes = S3UploadRetryManagerTest.TestConfig.class)
@RecordApplicationEvents // 이벤트 기록 활성화
class S3UploadRetryManagerTest {

  // S3UploadRetryManager Spring 컨텍스트에 빈으로 등록
  @Configuration
  @EnableRetry // @Retryable, @Recover 가 동작하는 AOP 프록시를 활성화
  static class TestConfig {

    @Bean
    S3UploadRetryManager s3UploadRetryManager(
        BinaryContentStorage storage,
        NotificationService notificationService,
        UserRepository userRepository,
        ApplicationEventPublisher publisher
    ) {
      return new S3UploadRetryManager(storage, notificationService, userRepository, publisher);
    }
  }

  @MockitoBean
  private BinaryContentStorage binaryContentStorage;
  @MockitoBean
  private NotificationService notificationService;
  @MockitoBean
  private UserRepository userRepository;

  @Autowired
  private S3UploadRetryManager s3UploadRetryManager;
  @Autowired
  private ApplicationEvents events;

  private final UUID binaryContentId = UUID.randomUUID();
  private final byte[] bytes = {1, 2, 3};

  // 이후 테스트에 영향을 주지 않도록 각 테스트 후 MDC를 초기화
  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  // S3UploadException 생성 헬퍼
  private S3UploadException uploadFailure() {
    return new S3UploadException("S3에 파일 업로드 실패: " + binaryContentId,
        new RuntimeException("The AWS Access Key Id you provided does not exist"));
  }

  @Test
  @DisplayName("업로드가 계속 실패하면 최대 3회 시도 후 실패 이벤트를 발행하고 예외를 재전파한다")
  void upload_alwaysFails_retriesThreeTimesThenRecovers() {
    // given: 모든 put() 호출이 항상 예외를 던지도록 stub
    given(binaryContentStorage.put(any(), any())).willThrow(uploadFailure());

    // when
    // 람다 실행 중 예외 발생하면 catchThrowable이 잡아서 thrown 변수에 저장
    Throwable thrown = catchThrowable(() -> s3UploadRetryManager.upload(binaryContentId, bytes));

    // then
    assertThat(thrown).isInstanceOf(S3UploadException.class); // 예외가 S3UploadException 인지
    // 3번 호출 되었는지
    verify(binaryContentStorage, times(3)).put(eq(binaryContentId), eq(bytes));
    // 3번의 재시도가 모두 실패한 후 @Recover에서 실패 이벤트(S3UploadFailedEvent)가 1번만 발행되었는지
    assertThat(events.stream(S3UploadFailedEvent.class).toList()).hasSize(1);
  }

  @Test
  @DisplayName("두 번 실패 후 세 번째에 성공하면 정상 반환하고 실패 이벤트를 발행하지 않는다")
  void upload_failsTwiceThenSucceeds_returnsIdWithoutEvent() {
    // given
    given(binaryContentStorage.put(any(), any()))
        .willThrow(uploadFailure())
        .willThrow(uploadFailure()) // 2차 재시도까지 싪패 후
        .willReturn(binaryContentId); // 3차 재시도 성공

    // when
    UUID result = s3UploadRetryManager.upload(binaryContentId, bytes);

    // then
    assertThat(result).isEqualTo(binaryContentId); // 정상 결과 반환하는지
    verify(binaryContentStorage, times(3)).put(eq(binaryContentId), eq(bytes));
    // 실패 이벤트가 없는지
    assertThat(events.stream(S3UploadFailedEvent.class).toList()).isEmpty();
  }

  @Test
  @DisplayName("첫 시도에 성공하면 재시도 없이 한 번만 호출하고 실패 이벤트를 발행하지 않는다")
  void upload_succeedsFirstTry_callsOnce() {
    // given: 첫 번째 호출부터 바로 성공
    given(binaryContentStorage.put(any(), any())).willReturn(binaryContentId);

    // when
    UUID result = s3UploadRetryManager.upload(binaryContentId, bytes);

    // then
    assertThat(result).isEqualTo(binaryContentId);
    // 재시도 없이 1번 호출
    verify(binaryContentStorage, times(1)).put(eq(binaryContentId), eq(bytes));
    assertThat(events.stream(S3UploadFailedEvent.class).toList()).isEmpty();
  }

  @Test
  @DisplayName("최종 실패 시 발행되는 이벤트에 MDC RequestId 와 BinaryContentId 가 담긴다")
  void recover_publishesEventWithMdcRequestId() {
    // given
    MDC.put(MDCLoggingInterceptor.REQUEST_ID, "req-123");
    given(binaryContentStorage.put(any(), any())).willThrow(uploadFailure());

    // when
    Throwable thrown = catchThrowable(() -> s3UploadRetryManager.upload(binaryContentId, bytes));

    // then
    assertThat(thrown).isInstanceOf(S3UploadException.class);
    List<S3UploadFailedEvent> published = events.stream(S3UploadFailedEvent.class).toList();
    assertThat(published).hasSize(1);
    assertThat(published.get(0).getRequestId()).isEqualTo("req-123"); // requestId 검증
    assertThat(published.get(0).getBinaryContentId()).isEqualTo(
        binaryContentId); // 실패한 파일 ID 기록 되었는지
  }
}

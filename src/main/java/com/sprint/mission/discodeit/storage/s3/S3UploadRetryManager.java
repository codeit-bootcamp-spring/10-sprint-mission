package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3UploadRetryManager {

  private final BinaryContentStorage binaryContentStorage;
  private final NotificationService notificationService;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  // https://github.com/spring-projects/spring-retry
  // S3에 파일을 업로드
  @Retryable(
      retryFor = S3UploadException.class, // S3UploadException 발생 시에만 재시도
      maxAttempts = 3, // 최대 3회 시도
      // 지수 백오프 전략으로 재시도 간격을 점진적으로 늘린다.
      // dealy=1000; 첫 번째 재시도 전 1000ms(1초) 대기
      // multiplie=2; 이후 재시도마다 대기 시간을 2배씩 증가
      // maxDelay=5000; 대기 시간의 상한선을 5000ms로 제한
      // random=true; 계산된 대기 시간에 0~1 사이의 랜덤 계수를 곱해 지터(의도적 타이밍 불규칙성)를 추가
      //              ex) 계산된 대기시간이 2000ms라면 실제 대기시간은 0~2000ms 사이 랜덤값
      backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000, random = true))
  public UUID upload(UUID binaryContentId, byte[] bytes) {
    return binaryContentStorage.put(binaryContentId, bytes);
  }

  /*
  @Retryable이 재시도를 모두 소진했을 때 호출되는 복구 메서드

  @Recover 메서드의 규칙
  - 첫번째 파라미터는 반드시 @Retryable 메서드가 던진 예외 타입이어야 한다.
  (생략 가능이긴 하나, 다른 매칭되는 @Recover 메서드가 없을 때만 호출됨)
  - 이후 파라미터는 @Retryable 메서드의 파라미터와 동일한 순서/타입이어야 한다.
  - 반환 타입도 @Retryable 메서드와 동일해야 한다. (여기서는 UUID)
   */
  @Recover
  public UUID recover(S3UploadException e, UUID binaryContentId, byte[] bytes) {
    String requestId = MDC.get(MDCLoggingInterceptor.REQUEST_ID);

    log.error("S3 업로드 최종 실패 - requestId={}, binaryContentId={}, error={}",
        requestId, binaryContentId, e.getMessage(), e);

    applicationEventPublisher.publishEvent(
        new S3UploadFailedEvent(binaryContentId, e, requestId)
    );

    throw e; // 예외를 던져야 handleBinaryContentCreated()에서 FAIL 상태 기록이 가능함
  }
}

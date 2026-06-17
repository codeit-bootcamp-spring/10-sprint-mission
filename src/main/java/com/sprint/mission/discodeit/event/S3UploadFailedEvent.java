package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.config.web.interceptor.MDCLoggingInterceptor;
import lombok.Getter;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.UUID;

// S3에 파일 업로드 실패 시 알림 발생을 요청하는 이벤트 클래스
@Getter
public class S3UploadFailedEvent {

    private final String requestId;
    private final UUID binaryContentId;
    private final Throwable error;

    // 이벤트 생성 시간
    private final Instant occurredAt;

    // 이벤트 생성자
    public S3UploadFailedEvent(
            UUID binaryContentId,
            Throwable error
    ) {
        this.requestId = MDC.get(MDCLoggingInterceptor.MDC_REQUEST_ID);
        this.binaryContentId = binaryContentId;
        this.error = error;

        this.occurredAt = Instant.now();
    }
}

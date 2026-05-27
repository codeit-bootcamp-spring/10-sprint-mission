package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {
    private final Instant timestamp;
    private final ErrorCode errorCode;

    // 예외 발생 상황에 대한 추가 정보 저장 필드
    // 조회 시도한 사용자ID, 업데이트 시도한 Private 채널의 ID
    private final Map<String, Object> details;

    protected DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage()); // `ErrorCode` 예외 메시지를 표준 예외 메시지로 등록
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }

    protected DiscodeitException(ErrorCode errorCode, String key, Object value) {
        super(errorCode.getMessage()); // `ErrorCode` 예외 메시지를 표준 예외 메시지로 등록
        this.timestamp = Instant.now();
        this.errorCode = errorCode;

        this.details = new HashMap<>();
        details.put(key, value);
    }

    protected DiscodeitException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        // 도메인에 맞는 커스텀 예외로 바꾸되 원래 예외는 잃지 않기 위해 `cause` 추가
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }

    protected DiscodeitException(ErrorCode errorCode, String key, Object value, Throwable cause) {
        // 도메인에 맞는 커스텀 예외로 바꾸되 원래 예외는 잃지 않기 위해 `cause` 추가
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;

        this.details = new HashMap<>();
        details.put(key, value);
    }
}

package com.sprint.mission.discodeit.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private final Instant timestamp;            // 에러 발생 시간
    private final String code;                  // 에러 코드 (ex: U001)
    private final String message;               // 에러 메시지
    private final Map<String, Object> details;  // 상세 정보 (ID, 필드명 등)
    private final String exceptionType;         // 예외 클래스 이름
    private final int status;                   // HTTP 상태 코드
}

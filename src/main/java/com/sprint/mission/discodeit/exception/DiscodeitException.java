package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details; // 예외 발생 상황에 대한 추가 정보

  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    this(errorCode, details, null);
  }

  // 예외 객체 같이 던질 때 사용
  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
    super(errorCode.getMessage(), cause); // 부모(RuntimeException)에게 메시지 전달
    this.timestamp = Instant.now(); // 에러가 발생한 시간 기록
    this.errorCode = errorCode;
    this.details = (details != null) ? Map.copyOf(details) : Map.of(); // null 체크, 불변성 보장을 위해 Map 복사
  }
}

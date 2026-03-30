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

  // 에러 코드만 던질 때 사용
  public DiscodeitException(ErrorCode errorCode) {
    this(errorCode, new HashMap<>());
  }

  // 추가 정보 같이 던질 때 사용
  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage()); // 부모(RuntimeException)에게 기본 메시지 전달
    this.timestamp = Instant.now(); // 에러가 발생한 시간 기록
    this.errorCode = errorCode;
    this.details = (details != null) ? details : new HashMap<>(); // null 방지
  }

  // 예외 객체 같이 던질 때 사용
  public DiscodeitException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = new java.util.HashMap<>();
  }
}

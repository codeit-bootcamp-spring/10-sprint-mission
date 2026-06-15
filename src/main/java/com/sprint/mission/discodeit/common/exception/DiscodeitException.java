package com.sprint.mission.discodeit.common.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public abstract class DiscodeitException extends RuntimeException {

  private final Map<String, Object> details = new HashMap<>();
  private final ErrorCode errorCode;

  public DiscodeitException(ErrorCode errorCode, Map<String, ?> detail) {
    this(errorCode);
    details.putAll(detail);
  }

  public DiscodeitException(ErrorCode errorCode, UUID id) {
    this(errorCode);
    details.put(getExceptionType(), id);
  }

  public DiscodeitException(ErrorCode errorCode, Exception exception) {
    this(errorCode, Map.of(exception.getClass().getSimpleName(), exception.getMessage()));
  }

  public DiscodeitException(ErrorCode errorCode, Object key, Object value) {
    this(errorCode, Map.of(key.toString(), value));
  }

  @Override
  public String getMessage() {
    return errorCode.getMessage();
  }

  public String getCode() {
    return errorCode.getCode();
  }

  public HttpStatus getStatus() {
    return errorCode.getStatus();
  }

  public String getExceptionType() {
    return getClass().getSimpleName();
  }

  public Map<String, Object> getDetails() {
    return details;
  }
}

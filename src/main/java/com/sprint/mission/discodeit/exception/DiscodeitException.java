package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class DiscodeitException extends RuntimeException {

  private final Map<String, String> details = new HashMap<>();
  private final Instant timestamp = Instant.now();
  private final ErrorCode errorCode;

  public DiscodeitException(ErrorCode errorCode, Map<String, ?> detail) {
    this(errorCode);
    addDetail(detail);
  }

  public DiscodeitException(ErrorCode errorCode, UUID id) {
    this(errorCode);
  }

  public DiscodeitException(ErrorCode errorCode, Exception exception) {
    this(errorCode, Map.of(exception.getClass().getSimpleName(), exception.getMessage()));
  }

  public void addDetail(Map<String, ?> detail) {
    Map<String, String> toStringMap = detail.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> String.valueOf(entry.getValue())
        ));
    details.putAll(toStringMap);
  }

  @Override
  public String getMessage() {
    return errorCode.getMessage();
  }

  public String getCode() {
    return errorCode.getCode();
  }

  public int getStatus() {
    return errorCode.getStatus();
  }

  public String getExceptionType() {
    return getClass().getSimpleName();
  }
}

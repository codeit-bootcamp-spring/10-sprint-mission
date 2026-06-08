package com.sprint.mission.discodeit.common.exception.common;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Internal server error"),
  OPTIMISTIC_LOCKING_FAILURE(HttpStatus.BAD_REQUEST, "C002",
      "Optimistic Locking Failure exception"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

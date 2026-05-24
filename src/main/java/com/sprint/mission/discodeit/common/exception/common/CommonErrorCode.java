package com.sprint.mission.discodeit.common.exception.common;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
  INTERNAL_SERVER_ERROR(500, "C001", "Internal server error"),
  OPTIMISTIC_LOCKING_FAILURE(400, "C002", "Optimistic Locking Failure exception"),
  ;

  private final int status;
  private final String code;
  private final String message;
}

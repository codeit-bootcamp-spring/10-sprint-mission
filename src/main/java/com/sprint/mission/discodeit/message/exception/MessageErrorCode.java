package com.sprint.mission.discodeit.message.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageErrorCode implements ErrorCode {
  MESSAGEID_NOT_FOUND(404, "M001", "Message id not found"),
  ;

  private final int status;
  private final String code;
  private final String message;
}

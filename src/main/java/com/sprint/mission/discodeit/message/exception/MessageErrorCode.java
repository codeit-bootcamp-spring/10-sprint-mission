package com.sprint.mission.discodeit.message.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MessageErrorCode implements ErrorCode {
  MESSAGEID_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "Message id not found"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

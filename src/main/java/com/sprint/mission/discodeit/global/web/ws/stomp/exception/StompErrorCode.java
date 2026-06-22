package com.sprint.mission.discodeit.global.web.ws.stomp.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StompErrorCode implements ErrorCode {
  DESTINATION_INCORRECT_FORMAT(HttpStatus.BAD_REQUEST, "S001", "destination incorrect format"),
  INCORRECT_ID(HttpStatus.BAD_REQUEST, "S002", "incorrect id"),
  ;
  private final HttpStatus status;
  private final String code;
  private final String message;
}

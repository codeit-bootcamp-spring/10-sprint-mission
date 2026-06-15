package com.sprint.mission.discodeit.notification.domain.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "N001", "unauthorized request"),
  UNAUTHENTICATED(HttpStatus.FORBIDDEN, "N002", "forbidden request"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "N003", "notification not found"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

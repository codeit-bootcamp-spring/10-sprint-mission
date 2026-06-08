package com.sprint.mission.discodeit.auth.domain.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

  COMPROMISED_TOKEN_DETECTED(HttpStatus.UNAUTHORIZED, "A001",
      "Compromised token detected. You have been logged out from all devices for security."),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A002",
      "The refresh token is invalid or has expired."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "The token has expired."),
  MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "A004",
      "The token is malformed or has been tampered with."),
  INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "A005", "The token signature is invalid."),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "A006", "The token format is not supported."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "A007", "No active tokens found for this user."),
  ;
  private final HttpStatus status;
  private final String code;
  private final String message;
}

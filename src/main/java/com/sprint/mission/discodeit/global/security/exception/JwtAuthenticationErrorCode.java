package com.sprint.mission.discodeit.global.security.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtAuthenticationErrorCode implements ErrorCode {

  TOKEN_CANT_BE_PARSED(HttpStatus.BAD_REQUEST, "J001",
      "Malformed JWT token string. Failed to parse."),
  UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "J002", "The provided JWT token is not supported."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "J003", "Invalid JWT token or signature mismatch."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "J004", "The provided JWT token has expired."),
  PAYLOAD_CANT_BE_PARSED(HttpStatus.UNAUTHORIZED, "J005",
      "Failed to extract claims from JWT payload."),
  ROLE_NOT_FOUND(HttpStatus.UNAUTHORIZED, "J006",
      "JWT payload does not contain required authorities claim."),
  TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "J007", "Refresh token not found"),
  ACTIVE_DEVICE_CHANGED(HttpStatus.CONFLICT, "J008",
      "The connection has disconnected as connected from another"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

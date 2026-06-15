package com.sprint.mission.discodeit.auth.domain.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserCredentialErrorCode implements ErrorCode {
  USER_CREDENTIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "UC001", "User credential not found"),
  USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "UC002", "no username match user credential"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

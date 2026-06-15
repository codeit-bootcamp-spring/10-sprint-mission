package com.sprint.mission.discodeit.readstatus.domain.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReadStatusErrorCode implements ErrorCode {
  READSTATUSID_NOT_FOUND(HttpStatus.NOT_FOUND, "RS001", "ReadStatus id not found"),
  READSTATUS_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "RS002", "ReadStatus already exists"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

package com.sprint.mission.discodeit.readstatus.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReadStatusErrorCode implements ErrorCode {
  READSTATUSID_NOT_FOUND(404, "RS001", "ReadStatus id not found"),
  READSTATUS_ALREADY_EXIST(400, "RS002", "ReadStatus already exists"),
  ;

  private final int status;
  private final String code;
  private final String message;
}

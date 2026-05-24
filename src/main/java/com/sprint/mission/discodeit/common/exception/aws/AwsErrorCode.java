package com.sprint.mission.discodeit.common.exception.aws;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AwsErrorCode implements ErrorCode {
  FILE_ALREADY_EXIST(400, "A001", "File already exists"),
  S3_CONNECTION_FAILED(400, "A002", "S3 connection failed"),
  ;
  private final int status;
  private final String code;
  private final String message;
}

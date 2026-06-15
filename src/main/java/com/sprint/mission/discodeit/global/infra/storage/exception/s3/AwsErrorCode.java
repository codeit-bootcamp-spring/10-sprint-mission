package com.sprint.mission.discodeit.global.infra.storage.exception.s3;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AwsErrorCode implements ErrorCode {
  FILE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "A001", "File already exists"),
  S3_CONNECTION_FAILED(HttpStatus.BAD_REQUEST, "A002", "S3 connection failed"),
  FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "A003", "File not found"),
  ;
  private final HttpStatus status;
  private final String code;
  private final String message;
}

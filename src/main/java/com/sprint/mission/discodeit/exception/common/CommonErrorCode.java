package com.sprint.mission.discodeit.exception.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
  INTERNAL_SERVER_ERROR(500, "C001", "Internal server error"),
  FILE_CANT_READ(500, "C002", "Failed to read file"),
  ROOT_DIRECTORY_FAILED_TO_CREATE(500, "C003", "Failed to create root directories"),
  FILE_CANT_WRITE(500, "C004", "Failed to save file"),
  FILE_ALREADY_EXIST(500, "C005", "File already exists"),
  FILE_NOT_FOUND(500, "C006", "File not found"),
  OPTIMISTIC_LOCKING_FAILURE(400, "C007", "Optimistic Locking Failure exception"),
  ;

  private final int status;
  private final String code;
  private final String message;
}

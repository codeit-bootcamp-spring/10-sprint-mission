package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
  FILE_CANT_READ(500, "C001", "Failed to read file"),
  ROOT_DIRECTORY_FAILED_TO_CREATE(500, "C002", "Failed to create root directories"),
  FILE_CANT_WRITE(500, "C003", "Failed to save file"),
  FILE_ALREADY_EXIST(500, "C004", "File already exists"),
  FILE_NOT_FOUND(500, "C005", "File not found"),
  ;

  private final int status;
  private final String code;
  private final String message;
}

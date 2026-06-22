package com.sprint.mission.discodeit.global.infra.storage.exception.local;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
  FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F001",
      "An error occurred while reading the file from the server."),
  ROOT_DIRECTORY_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F002",
      "Failed to initialize the root file storage directory."),
  FILE_WRITE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F003",
      "An error occurred while saving the file to the server."),
  FILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "F004", "A file with the same name already exists."),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "F005", "The requested file could not be found."),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

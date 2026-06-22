package com.sprint.mission.discodeit.binarycontent.domain.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BinaryContentErrorCode implements ErrorCode {
  BINARYCONTENTID_NOT_FOUND(HttpStatus.NOT_FOUND, "BC001", "BinaryContent id not found"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

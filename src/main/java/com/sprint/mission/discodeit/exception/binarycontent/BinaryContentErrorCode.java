package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BinaryContentErrorCode implements ErrorCode {
  BINARYCONTENTID_NOT_FOUND(404, "BC001", "BinaryContent id not found"),
  ;

  private final int status;
  private final String code;
  private final String message;
}

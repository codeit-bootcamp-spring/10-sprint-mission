package com.sprint.mission.discodeit.exception.s3;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class S3Exception extends DiscodeitException {

  public S3Exception(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }

  public S3Exception(ErrorCode errorCode) {
    this(errorCode, null);
  }
}

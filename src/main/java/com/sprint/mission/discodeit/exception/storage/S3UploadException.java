package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class S3UploadException extends DiscodeitException {
    public S3UploadException(ErrorCode errorCode) {
        super(errorCode);
    }

  public S3UploadException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }

  public static S3UploadException withId(UUID binaryContentId, Throwable cause) {
    S3UploadException exception = new S3UploadException(ErrorCode.S3_UPLOAD_FAILED, cause);
    exception.addDetail("binaryContentId", binaryContentId);
    return exception;
  }
}

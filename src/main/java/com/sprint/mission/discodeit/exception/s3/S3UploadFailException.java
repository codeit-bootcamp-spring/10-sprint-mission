package com.sprint.mission.discodeit.exception.s3;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class S3UploadFailException extends S3Exception {

  public S3UploadFailException(Throwable cause) {
    super(ErrorCode.AWS_S3_UPLOAD_FAIL, cause);
  }

  public S3UploadFailException() {
    this(null);
  }
}

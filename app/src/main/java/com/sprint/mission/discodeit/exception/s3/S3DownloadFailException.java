package com.sprint.mission.discodeit.exception.s3;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class S3DownloadFailException extends S3Exception {

  public S3DownloadFailException(Throwable cause) {
    super(ErrorCode.AWS_S3_DOWNLOAD_FAIL, cause);
  }

  public S3DownloadFailException() {
    this(null);
  }
}

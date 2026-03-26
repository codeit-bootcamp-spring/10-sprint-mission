package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import java.util.Map;

public class BinaryContentUploadException extends ChannelException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.BINARY_CONTENT_UPLOAD_FAILED;

  public BinaryContentUploadException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public BinaryContentUploadException() {
    super(DEFAULT_CODE);
  }

  public BinaryContentUploadException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public BinaryContentUploadException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}

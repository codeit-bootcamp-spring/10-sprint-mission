package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import java.util.Map;

public class BinaryContentDownloadException extends BinaryContentException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.BINARY_CONTENT_DOWNLOAD_FAILED;

  public BinaryContentDownloadException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public BinaryContentDownloadException() {
    super(DEFAULT_CODE);
  }

  public BinaryContentDownloadException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public BinaryContentDownloadException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}

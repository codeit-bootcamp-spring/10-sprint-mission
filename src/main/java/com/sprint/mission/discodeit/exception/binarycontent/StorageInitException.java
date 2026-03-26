package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import java.util.Map;

public class StorageInitException extends BinaryContentException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.STORAGE_INITIALIZATION_FAILED;

  public StorageInitException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public StorageInitException() {
    super(DEFAULT_CODE);
  }

  public StorageInitException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public StorageInitException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}

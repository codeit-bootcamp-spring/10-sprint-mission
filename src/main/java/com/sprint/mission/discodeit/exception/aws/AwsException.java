package com.sprint.mission.discodeit.exception.aws;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class AwsException extends DiscodeitException {

  public AwsException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AwsException(ErrorCode errorCode, Exception exception) {
    super(errorCode, exception);
  }

  public AwsException(ErrorCode errorCode, UUID id) {
    super(errorCode, Map.of("File id", id));
  }
}

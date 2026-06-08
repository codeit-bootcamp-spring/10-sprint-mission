package com.sprint.mission.discodeit.common.exception.aws;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class AwsException extends DiscodeitException {

  public AwsException(ErrorCode errorCode, Exception exception) {
    super(errorCode, exception);
  }

  public AwsException(ErrorCode errorCode, UUID id) {
    super(errorCode, Map.of("File id", id));
  }
}

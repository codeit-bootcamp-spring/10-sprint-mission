package com.codeit.mission.deokhugam.dashboard.exceptions;

import com.codeit.mission.deokhugam.error.DeokhugamException;
import com.codeit.mission.deokhugam.error.ErrorCode;
import java.util.Map;

public class InvalidKeepCountException extends DeokhugamException {

  public InvalidKeepCountException(int keepCount) {
    super(ErrorCode.KEEP_COUNT_INVALID, Map.of("keepCount", keepCount));
  }
}

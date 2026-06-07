package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

// FIXME: 커스텀 예외로 JwtTokenProvider 변경
public class InvalidTokenException extends DiscodeitException {

  public InvalidTokenException() {
    super(ErrorCode.INVALID_TOKEN);
  }
}

package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class AlreadyParticipatingException extends ChannelException {

  public AlreadyParticipatingException() {
    super(ErrorCode.ALREADY_PARTICIPATING);
  }

  public AlreadyParticipatingException(Map<String, Object> details) {
    super(ErrorCode.ALREADY_PARTICIPATING, details);
  }
}

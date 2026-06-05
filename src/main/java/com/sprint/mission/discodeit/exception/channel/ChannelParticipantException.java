package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelParticipantException extends ChannelException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.NOT_A_CHANNEL_PARTICIPANT;

  public ChannelParticipantException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public ChannelParticipantException() {
    super(DEFAULT_CODE);
  }

  public ChannelParticipantException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public ChannelParticipantException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}

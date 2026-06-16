package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PrivateChannelAlreadyExistsException extends ChannelException {

  private PrivateChannelAlreadyExistsException(Map<String, Object> details) {
    super(ErrorCode.PRIVATE_CHANNEL_ALREADY_EXISTS, details );
  }

  public static PrivateChannelAlreadyExistsException withParticipantIds(Set<UUID> participantIds) {
    return new PrivateChannelAlreadyExistsException(Map.of("participantIds", participantIds));
  }
}

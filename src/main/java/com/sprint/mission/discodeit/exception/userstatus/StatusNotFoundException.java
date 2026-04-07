package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class StatusNotFoundException extends UserStatusException {

  public StatusNotFoundException(UUID id) {
    super(ErrorCode.STATUS_NOT_FOUND, Map.of("statusId", id));

  }

}

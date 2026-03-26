package com.sprint.mission.discodeit.exception.status.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {

    public UserStatusNotFoundException(UUID statusId) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of("statusId", statusId));
    }
}

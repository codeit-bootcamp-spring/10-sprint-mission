package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistException extends UserStatusException {
    public UserStatusAlreadyExistException(UUID statusId) {
        super(ErrorCode.DUPLICATE_USER_STATUS, Map.of("userStatusId", statusId));
    }
}

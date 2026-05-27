package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class DuplicatedUsernameException extends UserException {

    public DuplicatedUsernameException(String username) {
        super(ErrorCode.DUPLICATED_USERNAME, "username", username);
    }

    public DuplicatedUsernameException(UUID userId, String username) {
        super(ErrorCode.DUPLICATED_USERNAME, Map.of("userId", userId, "username", username));
    }
}

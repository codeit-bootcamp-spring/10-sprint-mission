package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class DuplicationUserException extends UserException {

    private DuplicationUserException(Map<String, Object> details) {
        super(ErrorCode.DUPLICATE_USER, details);
    }

    public static DuplicationUserException withUserName(String userName) {
        return new DuplicationUserException(Map.of("username", userName));
    }

    public static DuplicationUserException withEmail(String email) {
        return new DuplicationUserException(Map.of("email", email));

    }
}

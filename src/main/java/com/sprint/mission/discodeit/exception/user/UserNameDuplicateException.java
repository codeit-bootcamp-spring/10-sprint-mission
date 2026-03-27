package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNameDuplicateException extends UserException {

    public UserNameDuplicateException() {
        super(ErrorCode.DUPLICATE_USER_NAME);
    }

    public UserNameDuplicateException(String name) {
        super(ErrorCode.DUPLICATE_USER_NAME, Map.of("username", name));
    }
}

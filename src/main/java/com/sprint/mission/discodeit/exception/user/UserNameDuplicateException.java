package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNameDuplicateException extends UserException {

    public UserNameDuplicateException() {
        super(ErrorCode.USER_NAME_DUPLICATE);
    }

    public UserNameDuplicateException(String name) {
        super(ErrorCode.USER_NAME_DUPLICATE, Map.of("username", name));
    }
}

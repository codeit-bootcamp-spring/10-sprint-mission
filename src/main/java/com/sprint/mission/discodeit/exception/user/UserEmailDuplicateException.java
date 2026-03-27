package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class UserEmailDuplicateException extends UserException {

    public UserEmailDuplicateException() {
        super(ErrorCode.DUPLICATE_USER_EMAIL);
    }

    public UserEmailDuplicateException(String email) {
        super(ErrorCode.DUPLICATE_USER_EMAIL, Map.of("email", email));
    }
}

package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserEmailDuplicateException extends UserException {

    public UserEmailDuplicateException() {
        super(ErrorCode.USER_EMAIL_DUPLICATE);
    }

    public UserEmailDuplicateException(String email) {
        super(ErrorCode.USER_EMAIL_DUPLICATE, Map.of("email", email));
    }
}

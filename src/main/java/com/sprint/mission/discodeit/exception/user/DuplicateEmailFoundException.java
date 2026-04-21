package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class DuplicateEmailFoundException extends UserException {
    public DuplicateEmailFoundException(String email) {
        super(ErrorCode.DUPLICATE_EMAIL, Map.of(
                "resourceId", email,
                "operation", "USER_REGISTRATION",
                "currentState", "EMAIL_ALREADY_EXISTS",
                "rule", "모든 사용자는 고유한 이메일 주소를 가져야 함"
        ));
    }
}

package com.sprint.mission.discodeit.exception.user;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends  UserException{

    public UserNotFoundException(String username) {
        super(ErrorCode.USER_NOT_FOUND, Map.of(
                "resourceId", username,
                "operation", "FIND_BY_USERNAME",
                "currentState", "NOT_FOUND_IN_DB",
                "rule", "입력된 username을 가진 사용자가 데이터베이스에 존재해야 함"
        ));
    }

    public UserNotFoundException(UUID userId) {
        super(ErrorCode.USER_NOT_FOUND, Map.of(
                "resourceId", userId.toString(),
                "operation", "FIND_BY_ID",
                "currentState", "NOT_FOUND_IN_DB",
                "rule", "유효한 UUID에 해당하는 사용자가 존재해야 함"
        ));
    }
}

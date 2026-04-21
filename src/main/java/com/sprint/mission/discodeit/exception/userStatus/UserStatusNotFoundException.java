package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {
    public UserStatusNotFoundException(UUID userId) {
        super(ErrorCode.USERSTATUS_NOT_FOUND, Map.of(
                "resourceId", userId.toString(),
                "operation", "GET_USER_STATUS",
                "currentState", "STATUS_NOT_FOUND",
                "rule", "조회하려는 사용자의 접속 상태 정보가 DB에 존재해야 함"
        ));
    }
}

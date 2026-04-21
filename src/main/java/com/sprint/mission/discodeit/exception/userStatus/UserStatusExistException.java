package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusExistException extends UserStatusException{

    public UserStatusExistException(UUID userId) {
        super(ErrorCode.USERSTATUS_EXIST, Map.of(
                "resourceId", userId.toString(),
                "operation", "INITIALIZE_USER_STATUS",
                "currentState", "STATUS_ALREADY_INITIALIZED",
                "rule", "사용자의 온라인 상태 정보는 중복으로 생성될 수 없음"
        ));
    }
}

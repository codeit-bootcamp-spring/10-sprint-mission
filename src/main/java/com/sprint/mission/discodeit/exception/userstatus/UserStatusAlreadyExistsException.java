package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistsException extends UserStatusException{
    private UserStatusAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.USER_STATUS_ALREADY_EXISTS, details);
    }

    public static UserStatusAlreadyExistsException withUserId(UUID userId) {
        return new UserStatusAlreadyExistsException(Map.of(
                "userId", userId,
                "reason", "이미 해당 유저의 UserStatus가 존재합니다."
        ));
    }


}

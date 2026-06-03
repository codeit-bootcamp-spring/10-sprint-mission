package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    UserNotFoundException
    ---------------------
    특정 사용자가 시스템 내 존재하지 않을 때 발생하는 예외 클래스
 */
public class UserNotFoundException extends UserException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(String username) {
        super(
                ErrorCode.USER_NOT_FOUND,
                Map.of("username", username)
        );
    }

    public UserNotFoundException(UUID userId) {
        super(
                ErrorCode.USER_NOT_FOUND,
                Map.of("userId", userId)
        );
    }
}

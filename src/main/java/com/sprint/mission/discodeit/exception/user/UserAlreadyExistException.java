package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserAlreadyExistException extends UserException {

    // 기본 생성자
    private UserAlreadyExistException(ErrorCode errorCode, String value) {
        super(errorCode, Map.of("duplicateValue", value));
    }

    // 이메일 중복
    public static UserAlreadyExistException email(String email) {
        return new UserAlreadyExistException(ErrorCode.DUPLICATE_EMAIL, email);
    }

    // 유저명 중복
    public static UserAlreadyExistException username(String username) {
        return new UserAlreadyExistException(ErrorCode.DUPLICATE_USERNAME, username);
    }
}

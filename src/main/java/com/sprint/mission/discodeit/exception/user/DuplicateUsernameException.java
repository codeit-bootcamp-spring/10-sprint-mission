package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/*
    DuplicateUsernameException
    --------------------------
    사용자 회원가입에서 사용자 닉네임이 중복될 때 발생하는 예외 클래스
 */
public class DuplicateUsernameException extends UserException {

    public DuplicateUsernameException(String newUsername) {
        super(
                ErrorCode.DUPLICATE_USERNAME,
                Map.of("username", newUsername)
        );
    }
}

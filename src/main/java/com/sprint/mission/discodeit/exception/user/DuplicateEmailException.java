package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/*
    DuplicateEmailException
    -----------------------
    사용자 회원가입에서 이메일이 중복될 때 발생하는 예외 클래스
 */
public class DuplicateEmailException extends UserException {

    public DuplicateEmailException(String newEmail) {
        super(
                ErrorCode.DUPLICATE_EMAIL,
                Map.of("email", newEmail)
        );
    }
}

package com.sprint.mission.discodeit.exception.others;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class WrongPasswordException extends DiscodeitException {
    public WrongPasswordException() {
        super(ErrorCode.WRONG_PASSWORD, Map.of(
                "resourceId", "PROTECTED_DATA",
                "operation", "USER_AUTHENTICATION",
                "currentState", "PASSWORD_MISMATCH",
                "rule", "입력된 비밀번호가 저장된 사용자 정보와 일치해야 함"
        ));
    }
}

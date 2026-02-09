package com.sprint.mission.discodeit.user.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class AuthenticationFailedException extends BusinessException {

    public AuthenticationFailedException() {
        super("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}

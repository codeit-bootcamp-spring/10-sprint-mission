package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/*
    AuthException
    ----------------------
    Auth 도메인 내 최상위 예외 클래스
 */
public class AuthException extends DiscodeitException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
    public AuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

}

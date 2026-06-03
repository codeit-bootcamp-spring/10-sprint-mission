package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * 실제로 활용되는 클래스가 아닌 계층 구조를 명확히 하기 위한 클래스
 * <br>
 * ➡️ 추상 클래스(`abstract`)
 */
public abstract class SecurityException extends DiscodeitException {

    protected SecurityException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    protected SecurityException(ErrorCode errorCode, String key, Object value) {
        super(errorCode, key, value);
    }

    protected SecurityException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }

    protected SecurityException(ErrorCode errorCode, String key, Object value, Throwable cause) {
        super(errorCode, key, value, cause);
    }
}

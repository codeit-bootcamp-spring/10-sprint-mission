package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * 실제로 활용되는 클래스가 아닌 계층 구조를 명확히 하기 위한 클래스
 * <br>
 * ➡️ 추상 클래스(`abstract`)
 */
public abstract class UserException extends DiscodeitException {

    protected UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    protected UserException(ErrorCode errorCode, String key, Object value) {
        super(errorCode, key, value);
    }

    protected UserException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }

    protected UserException(ErrorCode errorCode, String key, Object value, Throwable cause) {
        super(errorCode, key, value, cause);
    }
}

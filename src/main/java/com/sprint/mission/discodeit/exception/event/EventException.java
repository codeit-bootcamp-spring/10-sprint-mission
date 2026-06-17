package com.sprint.mission.discodeit.exception.event;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

/**
 * 실제로 사용되는 클래스가 아닌 계층 구조를 명확히 히가 위한 클래스
 * <br>
 * ➡️ 추상 클래스(`abstract`)
 */
public abstract class EventException extends DiscodeitException {

    protected EventException(ErrorCode errorCode, String key, Object value, Throwable e) {
        super(errorCode, key, value, e);
    }
}

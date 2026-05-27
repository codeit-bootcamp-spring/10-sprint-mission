package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

/**
 * 실제로 활용되는 클래스가 아닌 계층 구조를 명확히 하기 위한 클래스
 * <br>
 * ➡️ 추상 클래스(`abstract`)
 */
public abstract class BinaryContentException extends DiscodeitException {

    protected BinaryContentException(ErrorCode errorCode, String key, Object value) {
        super(errorCode, key, value);
    }

    protected BinaryContentException(ErrorCode errorCode, String key, Object value, Throwable e) {
        super(errorCode, key, value, e);
    }
}

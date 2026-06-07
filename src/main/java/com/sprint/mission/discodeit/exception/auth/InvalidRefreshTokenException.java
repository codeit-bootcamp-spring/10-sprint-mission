package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidRefreshTokenException extends DiscodeitException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN, Map.of());
    }

    public InvalidRefreshTokenException(String reason) {
        super(ErrorCode.INVALID_REFRESH_TOKEN, Map.of("reason", reason));
    }
}

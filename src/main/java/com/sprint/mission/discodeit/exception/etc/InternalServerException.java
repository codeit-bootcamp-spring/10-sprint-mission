package com.sprint.mission.discodeit.exception.etc;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class InternalServerException extends DiscodeitException {

    private InternalServerException(Map<String, Object> details) {
        super(ErrorCode.DATA_INTEGRITY_ERROR, details);
    }

    public static InternalServerException dataIntegrity(String message, Object... args) {
        return new InternalServerException(Map.of("reason", String.format(message, args)));
    }
}

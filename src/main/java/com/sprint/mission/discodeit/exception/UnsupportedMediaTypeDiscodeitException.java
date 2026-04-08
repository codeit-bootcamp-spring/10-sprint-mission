package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class UnsupportedMediaTypeDiscodeitException extends DiscodeitException {

    public UnsupportedMediaTypeDiscodeitException(String message) {
        super(ErrorCode.UNSUPPORTED_MEDIA_TYPE, message);
    }

    public UnsupportedMediaTypeDiscodeitException(String message, Map<String, Object> details) {
        super(ErrorCode.UNSUPPORTED_MEDIA_TYPE, message, details);
    }

    public UnsupportedMediaTypeDiscodeitException(
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(ErrorCode.UNSUPPORTED_MEDIA_TYPE, message, details, cause);
    }
}

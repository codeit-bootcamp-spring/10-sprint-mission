package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ReadStatusNotFoundException extends ReadStatusException {

    public ReadStatusNotFoundException(String message) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, message);
    }

    public ReadStatusNotFoundException(String message, Map<String, Object> details) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, message, details);
    }
}

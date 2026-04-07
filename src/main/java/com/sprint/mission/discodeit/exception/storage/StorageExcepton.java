package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class StorageExcepton extends DiscodeitException {

    public StorageExcepton(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}

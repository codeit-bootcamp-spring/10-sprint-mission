package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BinaryContentAlreadyExistsException extends DiscodeitException {

    public BinaryContentAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.BINARYCONTENT_ALREADY_EXISTS, details);
    }
}

package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentAlreadyExistException extends BinaryContentException {

    public BinaryContentAlreadyExistException(UUID binaryContentId) {
        super(ErrorCode.DUPLICATE_BINARY_CONTENT, Map.of("binaryContentId", binaryContentId));
    }
}

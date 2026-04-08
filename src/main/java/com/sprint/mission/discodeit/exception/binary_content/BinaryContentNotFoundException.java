package com.sprint.mission.discodeit.exception.binary_content;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {

    public BinaryContentNotFoundException(UUID contentId) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("contentId", contentId));
    }
}

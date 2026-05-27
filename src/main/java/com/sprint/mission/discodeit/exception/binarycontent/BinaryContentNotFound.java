package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFound extends BinaryContentException{
    public BinaryContentNotFound(UUID binaryContentId){
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("binaryContentId", binaryContentId));
    }
}

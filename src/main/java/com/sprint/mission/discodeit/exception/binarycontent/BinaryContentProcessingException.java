package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BinaryContentProcessingException extends BinaryContentException{
    public BinaryContentProcessingException(String reason){
        super(ErrorCode.BINARY_CONTENT_PROCESSING_ERROR,
                Map.of("reason", reason));
    }
}

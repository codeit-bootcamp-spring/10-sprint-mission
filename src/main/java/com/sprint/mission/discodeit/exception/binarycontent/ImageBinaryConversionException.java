package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ImageBinaryConversionException extends BinaryContentException {

    public ImageBinaryConversionException(String message) {
        super(ErrorCode.IMAGE_BINARY_CONVERSION_FAILED, message);
    }

    public ImageBinaryConversionException(String message, Map<String, Object> details) {
        super(ErrorCode.IMAGE_BINARY_CONVERSION_FAILED, message, details);
    }

    public ImageBinaryConversionException(
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(ErrorCode.IMAGE_BINARY_CONVERSION_FAILED, message, details, cause);
    }
}

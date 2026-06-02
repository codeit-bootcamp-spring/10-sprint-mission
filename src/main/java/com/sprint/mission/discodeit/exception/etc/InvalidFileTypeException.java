package com.sprint.mission.discodeit.exception.etc;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidFileTypeException extends DiscodeitException {
    private InvalidFileTypeException(Map<String, Object> details) {
        super(ErrorCode.INVALID_FILE_TYPE, details);
    }

    public static InvalidFileTypeException imageOnly(String contentType) {
        return new InvalidFileTypeException(Map.of(
                "providedType", contentType != null ? contentType : "unknown",
                "expectedType", "image/*",
                "reason", "이미지 파일 확장자만 업로드할 수 있습니다."
        ));
    }
}

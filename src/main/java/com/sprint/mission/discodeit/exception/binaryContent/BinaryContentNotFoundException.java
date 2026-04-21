package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(UUID binaryContentId) {
        super(ErrorCode.BINARYCONTENT_NOT_FOUND, Map.of(
                "resourceId", binaryContentId.toString(),
                "operation", "GET_BINARY_METADATA",
                "currentState", "DATABASE_RECORD_MISSING",
                "rule", "조회하려는 바이너리 콘텐츠 정보가 DB에 존재해야 함"
        ));
    }
}

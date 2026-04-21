package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class FileReadFailException extends FileException {
    public FileReadFailException(UUID binaryContentId) {
        super(ErrorCode.FILE_READ_FAIL, Map.of(
                "resourceId", binaryContentId.toString(),
                "operation", "READ_FILE_FROM_STORAGE",
                "currentState", "FILE_NOT_ACCESSIBLE",
                "rule", "요청한 바이너리 ID에 해당하는 물리적 파일이 존재하고 읽기 권한이 있어야 함"
        ));
    }
}

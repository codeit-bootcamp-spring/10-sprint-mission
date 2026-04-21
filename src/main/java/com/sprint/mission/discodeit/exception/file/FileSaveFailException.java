package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class FileSaveFailException extends FileException {
    public FileSaveFailException(UUID binaryContentId) {
        super(ErrorCode.FILE_SAVE_FAIL, Map.of(
                "resourceId", binaryContentId.toString(),
                "operation", "PERSIST_FILE_TO_STORAGE",
                "currentState", "DISK_WRITE_FAILURE",
                "rule", "물리적 파일이 지정된 스토리지 경로에 성공적으로 기록되어야 함"
        ));
    }
}

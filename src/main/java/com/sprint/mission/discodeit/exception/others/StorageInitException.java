package com.sprint.mission.discodeit.exception.others;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.nio.file.Path;
import java.util.Map;

public class StorageInitException extends DiscodeitException {
    public StorageInitException(Path root, String reason) {
        super(ErrorCode.STORAGE_INIT_FAIL, Map.of(
                "resourceId", root.toString(),           // 문제가 된 경로
                "operation", "INITIALIZE_STORAGE_ROOT",  // 수행하려던 작업
                "currentState", "FILE_SYSTEM_ERROR",     // 현재 상태
                "rule", "스토리지 루트 디렉토리는 앱 시작 시 생성 가능하고 쓰기 권한이 있어야 함", // 비즈니스 규칙
                "reason", reason                         // 구체적인 IO 예외 메시지
        ));
    }
}

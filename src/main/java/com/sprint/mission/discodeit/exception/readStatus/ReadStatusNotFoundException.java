package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {
    public ReadStatusNotFoundException(UUID readStatusId) {
        super(ErrorCode.READSTATUS_NOT_FOUND, Map.of(
                "resourceId", readStatusId.toString(),
                "operation", "GET_READ_STATUS",
                "currentState", "NOT_FOUND",
                "rule", "업데이트하려는 읽음 상태 정보가 DB에 존재해야 함"
        ));
    }
}

package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ReadStatusExistException extends ReadStatusException {
    public ReadStatusExistException() {
        super(ErrorCode.READSTATUS_EXIST, Map.of(
                "resourceId", "USER_CHANNEL_PAIR",
                "operation", "CREATE_READ_STATUS",
                "currentState", "DUPLICATE_ENTRY",
                "rule", "동일한 채널에 대한 읽음 상태는 사용자당 하나만 존재해야 함"
        ));
    }
}

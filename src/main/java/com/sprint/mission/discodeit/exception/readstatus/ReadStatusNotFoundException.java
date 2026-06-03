package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    ReadStatusNotFoundException
    ---------------------------
    특정 읽음 상태가 시스템 내 존재하지 않을 때 발생하는 예외 클래스
 */
public class ReadStatusNotFoundException extends ReadStatusException {

    public ReadStatusNotFoundException(UUID readStatusId) {
        super(
                ErrorCode.READ_STATUS_NOT_FOUND,
                Map.of("readStatusId", readStatusId)
        );
    }

    public ReadStatusNotFoundException(UUID userId, UUID channelId) {
        super(
                ErrorCode.READ_STATUS_NOT_FOUND,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                )
        );
    }
}

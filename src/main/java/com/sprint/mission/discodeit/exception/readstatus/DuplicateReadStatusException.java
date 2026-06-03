package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    DuplicateReadStatusException
    ----------------------------
    특정 사용자와 특정 채널에 대한 사용자의 읽음 상태가 이미 존재할 때 발생하는 예외 클래스
 */
public class DuplicateReadStatusException extends ReadStatusException {

    public DuplicateReadStatusException(UUID userId, UUID channelId) {
        super(
                ErrorCode.DUPLICATE_READ_STATUS,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                )
        );
    }

}
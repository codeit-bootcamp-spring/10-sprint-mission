package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class DuplicatedReadStatusException extends ReadStatusException {

    public DuplicatedReadStatusException(UUID userId, UUID channelId) {
        super(ErrorCode.DUPLICATED_READ_STATUS, Map.of("userId", userId, "channeId", channelId));
    }
}

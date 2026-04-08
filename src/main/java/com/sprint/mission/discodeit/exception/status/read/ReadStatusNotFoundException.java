package com.sprint.mission.discodeit.exception.status.read;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException{

    public ReadStatusNotFoundException(UUID statusId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, Map.of("readStatusId", statusId));
    }

    public ReadStatusNotFoundException(UUID userId, UUID channelId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, Map.of("userId", userId, "channelId", channelId));
    }
}

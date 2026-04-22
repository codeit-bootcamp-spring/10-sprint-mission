package com.sprint.mission.discodeit.exception.read_status;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusAlreadyExistsException extends ReadStatusException {
    public ReadStatusAlreadyExistsException(UUID userId, UUID channelId) {
        super(ErrorCode.DUPLICATE_READ_STATUS, Map.of("userId", userId, "channelId", channelId));
    }
}
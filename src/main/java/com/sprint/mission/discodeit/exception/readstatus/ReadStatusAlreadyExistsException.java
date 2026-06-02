package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusAlreadyExistsException extends ReadStatusException {
    private ReadStatusAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.READ_STATUS_ALREADY_EXISTS, details);
    }

    public static ReadStatusAlreadyExistsException withUserAndChannel(UUID userId, UUID channelId) {
        return new ReadStatusAlreadyExistsException(Map.of(
                "userId", userId,
                "channelId", channelId,
                "reason", "해당 유저는 이미 이 채널의 읽기 상태를 가지고 있습니다."
        ));
    }
}

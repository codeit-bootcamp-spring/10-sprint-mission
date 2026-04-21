package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException(UUID messageId) {
        super(ErrorCode.MESSAGE_NOT_FOUND, Map.of(
                "resourceId", messageId.toString(),
                "operation", "GET_MESSAGE",
                "currentState", "MESSAGE_NOT_FOUND",
                "rule", "조회하거나 수정하려는 메시지가 해당 채널에 존재해야 함"
        ));
    }
}

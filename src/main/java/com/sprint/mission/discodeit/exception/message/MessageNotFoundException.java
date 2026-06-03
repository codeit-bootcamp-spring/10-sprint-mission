package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    MessageNotFoundException
    ------------------------
    해당 메시지가 시스템 내에 존재하지 않을 때 발생하는 예외 클래스
 */
public class MessageNotFoundException extends MessageException {

    public MessageNotFoundException(UUID messageId) {
        super(
                ErrorCode.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
        );
    }
}

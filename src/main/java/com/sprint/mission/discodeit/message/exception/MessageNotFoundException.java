package com.sprint.mission.discodeit.message.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class MessageNotFoundException extends BusinessException {
    public MessageNotFoundException() {
        super("해당 메시지를 찾을 수 없습니다.");
    }
}

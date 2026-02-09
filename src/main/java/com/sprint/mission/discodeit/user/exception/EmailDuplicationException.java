package com.sprint.mission.discodeit.user.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class EmailDuplicationException extends BusinessException {
    public EmailDuplicationException() {
        super("해당 이메일이 이미 존재합니다.");
    }
}

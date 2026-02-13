package com.sprint.mission.discodeit.user.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class UserDuplicationException extends BusinessException {
    public UserDuplicationException() {
        super("해당 사용자가 이미 존재합니다.");
    }
}

package com.sprint.mission.discodeit.user.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super("해당 사용자를 찾을 수 없습니다.");
    }
}

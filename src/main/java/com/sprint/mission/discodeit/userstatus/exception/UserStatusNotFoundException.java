package com.sprint.mission.discodeit.userstatus.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class UserStatusNotFoundException extends BusinessException {
    public UserStatusNotFoundException() {
        super("해당 사용자 상태를 찾을 수 없습니다.");
    }
}

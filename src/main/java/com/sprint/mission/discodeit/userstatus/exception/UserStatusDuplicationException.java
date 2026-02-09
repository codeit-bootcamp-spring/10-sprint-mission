package com.sprint.mission.discodeit.userstatus.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class UserStatusDuplicationException extends BusinessException {
    public UserStatusDuplicationException() {
        super("해당 사용자 상태가 이미 존재합니다.");
    }
}

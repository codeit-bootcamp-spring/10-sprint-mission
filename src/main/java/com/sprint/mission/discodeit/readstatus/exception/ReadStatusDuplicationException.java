package com.sprint.mission.discodeit.readstatus.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class ReadStatusDuplicationException extends BusinessException {
    public ReadStatusDuplicationException() {
        super("해당 수신 정보가 이미 존재합니다.");
    }
}

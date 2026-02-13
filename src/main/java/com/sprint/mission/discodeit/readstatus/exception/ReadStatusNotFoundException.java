package com.sprint.mission.discodeit.readstatus.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class ReadStatusNotFoundException extends BusinessException {
    public ReadStatusNotFoundException() {
        super("해당 수신 정보가 존재하지 않습니다.");
    }
}

package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class EmailAlreadyExistException extends UserException {

    public EmailAlreadyExistException(Map<String,Object> details) {
        super(ErrorCode.DUPLICATE_EMAIL,details);
    }
}

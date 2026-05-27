package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class DuplicatedEmailException extends UserException {

    public DuplicatedEmailException(String email) {
        super(ErrorCode.DUPLICATED_EMAIL, "email", email);
    }

    public DuplicatedEmailException(UUID userId, String email) {
        super(ErrorCode.DUPLICATED_EMAIL, Map.of("userId", userId, "email", email));
    }
}

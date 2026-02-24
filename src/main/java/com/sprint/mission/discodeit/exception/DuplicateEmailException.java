package com.sprint.mission.discodeit.exception;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super(ErrorCode.DUPLICATE_EMAIL, "User with email" + email + "already exists");
    }
}

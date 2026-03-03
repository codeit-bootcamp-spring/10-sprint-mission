package com.sprint.mission.discodeit.exception;

public class DuplicateUsernameException extends BusinessException{
    public DuplicateUsernameException(String username) {
        super(ErrorCode.DUPLICATE_USERNAME, "User with username" + username + "already exists");
    }

}

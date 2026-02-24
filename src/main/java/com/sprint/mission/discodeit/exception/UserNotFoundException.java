package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(UUID userId){
        super(ErrorCode.USER_NOT_FOUND,"User with id" + userId + "not found");
    }

    public UserNotFoundException(String username){
        super(ErrorCode.USER_NOT_FOUND, "User with username" + username + "not found");
    }
}

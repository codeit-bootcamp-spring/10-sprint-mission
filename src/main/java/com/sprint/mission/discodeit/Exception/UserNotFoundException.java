package com.sprint.mission.discodeit.Exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("해당하는 회원이 없습니다.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

}

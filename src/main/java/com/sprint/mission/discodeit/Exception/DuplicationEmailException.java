package com.sprint.mission.discodeit.Exception;

public class DuplicationEmailException extends RuntimeException {

    public DuplicationEmailException() {
        super("중복된 이메일입니다.");
    }

    public DuplicationEmailException(String message) {
        super(message);
    }



}

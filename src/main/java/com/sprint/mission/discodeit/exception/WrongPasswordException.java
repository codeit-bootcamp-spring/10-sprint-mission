package com.sprint.mission.discodeit.exception;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException() {
        super("Wrong password");
    }
}

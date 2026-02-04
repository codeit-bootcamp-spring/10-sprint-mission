package com.sprint.mission.discodeit.exception;

public class DuplicationUserException extends RuntimeException {
    public DuplicationUserException() {
        super("이미 사용중인 사용자 이름입니다.");
    }
}

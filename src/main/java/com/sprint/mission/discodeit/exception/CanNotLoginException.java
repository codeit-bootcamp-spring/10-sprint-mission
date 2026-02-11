package com.sprint.mission.discodeit.exception;

public class CanNotLoginException extends RuntimeException {
    public CanNotLoginException() {
        super("아이디 또는 비밀번호가 틀렸습니다.");
    }
}

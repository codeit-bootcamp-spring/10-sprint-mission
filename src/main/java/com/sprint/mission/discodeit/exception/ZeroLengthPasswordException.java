package com.sprint.mission.discodeit.exception;

public class ZeroLengthPasswordException extends RuntimeException {
    public ZeroLengthPasswordException() {
        super("길이가 0인 password는 생성할 수 없습니다.");
    }
}

package com.sprint.mission.discodeit.exception;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException() {
        super("유저 상태 확인 불가능");
    }
}

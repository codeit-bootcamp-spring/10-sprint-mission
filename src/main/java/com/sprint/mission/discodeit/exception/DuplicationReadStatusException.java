package com.sprint.mission.discodeit.exception;

public class DuplicationReadStatusException extends RuntimeException {
    public DuplicationReadStatusException() {
        super("아이디나 채널이 이미 존재합니다.");
    }
}

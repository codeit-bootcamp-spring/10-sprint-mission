package com.sprint.mission.discodeit.exception;


public class MessageNotInputException extends RuntimeException {
    public MessageNotInputException() {
        super("메시지가 입력되지 않았습니다.");
    }
}

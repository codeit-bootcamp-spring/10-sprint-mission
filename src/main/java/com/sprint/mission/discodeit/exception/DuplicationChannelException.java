package com.sprint.mission.discodeit.exception;


public class DuplicationChannelException extends RuntimeException {

    public DuplicationChannelException() {
        super("이미 해당 채널이 만들어져있습니다.");
    }

    public DuplicationChannelException(String message) {
        super(message);
    }


}

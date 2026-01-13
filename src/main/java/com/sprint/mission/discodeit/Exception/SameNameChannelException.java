package com.sprint.mission.discodeit.Exception;

public class SameNameChannelException extends RuntimeException {

    public SameNameChannelException() {
        super("이미 해당 채널이 만들어져있습니다.");
    }

    public SameNameChannelException(String message) {
        super(message);
    }


}

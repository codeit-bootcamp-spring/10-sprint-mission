package com.sprint.mission.discodeit.exception;


public class AlreadyJoinedChannelException extends RuntimeException {

    public AlreadyJoinedChannelException() {
        super("이미 해당 채널에 들어가 있습니다.");
    }

    public AlreadyJoinedChannelException(String message) {
        super(message);
    }

}

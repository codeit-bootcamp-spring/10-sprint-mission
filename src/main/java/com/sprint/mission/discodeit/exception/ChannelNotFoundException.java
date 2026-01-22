package com.sprint.mission.discodeit.exception;


public class ChannelNotFoundException extends RuntimeException {
    public ChannelNotFoundException() {
        super("해당하는 회원이 없습니다.");
    }
}

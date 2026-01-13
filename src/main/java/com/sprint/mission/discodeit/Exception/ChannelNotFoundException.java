package com.sprint.mission.discodeit.Exception;

import com.sprint.mission.discodeit.entity.Channel;

public class ChannelNotFoundException extends RuntimeException {

    public ChannelNotFoundException() {
        super("해당하는 회원이 없습니다.");
    }

    public ChannelNotFoundException(String message) {
        super(message);
    }

}

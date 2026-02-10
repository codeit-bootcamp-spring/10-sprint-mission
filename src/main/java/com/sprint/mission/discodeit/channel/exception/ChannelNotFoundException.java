package com.sprint.mission.discodeit.channel.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class ChannelNotFoundException extends BusinessException {
    public ChannelNotFoundException() {
        super("해당 채널을 찾을 수 없습니다.");
    }
}

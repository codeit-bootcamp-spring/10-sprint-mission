package com.sprint.mission.discodeit.channel.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class ChannelDuplicationException extends BusinessException {
    public ChannelDuplicationException() {
        super("해당 채널이 이미 존재합니다.");
    }
}

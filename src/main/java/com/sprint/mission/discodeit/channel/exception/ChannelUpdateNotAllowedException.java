package com.sprint.mission.discodeit.channel.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class ChannelUpdateNotAllowedException extends BusinessException {
    public ChannelUpdateNotAllowedException() {
        super("해당 채널은 수정할 수 없습니다.");
    }
}

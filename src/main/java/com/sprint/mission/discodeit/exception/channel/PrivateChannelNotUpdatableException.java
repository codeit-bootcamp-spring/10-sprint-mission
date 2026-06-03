package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/*
    PrivateChannelNotUpdatableException
    -----------------------------------
    비공개 채널을 변경하고자 할 때 발생하는 예외 클래스
 */
public class PrivateChannelNotUpdatableException extends ChannelException {

    public PrivateChannelNotUpdatableException() {
        super(ErrorCode.PRIVATE_CHANNEL_NOT_UPDATABLE);
    }
}

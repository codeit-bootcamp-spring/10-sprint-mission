package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    AccessDeniedPrivateChannelException
    -----------------------------------
    비공개 채널 멤버가 아닌 사용자가 접근하고자 할 때 발생하는 예외 클래스
 */
public class AccessDeniedPrivateChannelException extends ChannelException{

    public AccessDeniedPrivateChannelException(UUID userId, UUID channelId) {
        super(
                ErrorCode.ACCESS_DENIED_PRIVATE_CHANNEL,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                ));
    }
}

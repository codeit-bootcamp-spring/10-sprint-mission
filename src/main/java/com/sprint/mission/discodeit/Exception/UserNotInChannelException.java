package com.sprint.mission.discodeit.Exception;

import com.sprint.mission.discodeit.service.jcf.JCFChannelService;

public class UserNotInChannelException extends RuntimeException {
    public UserNotInChannelException(){
        super("유저가 해당 채널에 없습니다");
    }
    public UserNotInChannelException(String message) {
        super(message);
    }
}




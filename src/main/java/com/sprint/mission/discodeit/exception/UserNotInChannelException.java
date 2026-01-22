package com.sprint.mission.discodeit.exception;


public class UserNotInChannelException extends RuntimeException {
    public UserNotInChannelException(){
        super("유저가 해당 채널에 없습니다");
    }
}




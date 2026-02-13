package com.sprint.mission.discodeit.channel.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class AlreadyJoinedException extends BusinessException {
    public AlreadyJoinedException() {
        super("이미 가입된 유저입니다.");
    }
}

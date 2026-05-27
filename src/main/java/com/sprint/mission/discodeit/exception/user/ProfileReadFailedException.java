package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class ProfileReadFailedException extends UserException {

    public ProfileReadFailedException(UUID profileId, Throwable e) {
        super(ErrorCode.PROFILE_READ_FAILED, "profileId", profileId, e);
    }
}

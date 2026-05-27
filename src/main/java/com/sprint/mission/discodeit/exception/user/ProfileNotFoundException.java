package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class ProfileNotFoundException extends UserException {

    public ProfileNotFoundException(UUID profileId) {
        super(ErrorCode.PROFILE_NOT_FOUND, "profileId", profileId);
    }
}

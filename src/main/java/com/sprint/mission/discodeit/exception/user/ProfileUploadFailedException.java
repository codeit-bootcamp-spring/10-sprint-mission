package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ProfileUploadFailedException extends UserException {

    public ProfileUploadFailedException(UUID userId, Throwable e) {
        super(ErrorCode.PROFILE_UPLOAD_FAILED, "userId", userId, e);
    }

    public ProfileUploadFailedException(String email, String username, Throwable e) {
        super(ErrorCode.PROFILE_UPLOAD_FAILED, Map.of("email", email, "username", username), e);
    }
}

package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserInfoWithProfile(
        String userName,
        String email,
        UUID profileId
) {}

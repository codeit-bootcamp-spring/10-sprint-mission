package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserInfoWithProfile(
        UUID userId,
        String userName,
        String email,
        UUID profileId
) {}

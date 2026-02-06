package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserInfoWithStatus(
        UUID userId,
        String userName,
        String email,
        UUID profileId,
        UUID statusId,
        boolean isOnline
) {}

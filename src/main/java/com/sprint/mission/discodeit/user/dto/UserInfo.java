package com.sprint.mission.discodeit.user.dto;

import java.util.UUID;

public record UserInfo(
        UUID userId,
        String userName,
        String email,
        UUID profileId,
        UUID statusId
) {}

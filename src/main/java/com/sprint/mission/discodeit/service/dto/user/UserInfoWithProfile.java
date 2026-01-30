package com.sprint.mission.discodeit.service.dto.user;

import java.util.UUID;

public record UserInfoWithProfile(
        String userName,
        String email,
        UUID profileId
) {}

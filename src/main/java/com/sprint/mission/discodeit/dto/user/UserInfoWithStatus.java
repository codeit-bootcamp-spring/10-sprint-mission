package com.sprint.mission.discodeit.dto.user;

public record UserInfoWithStatus(
        String userName,
        String email,
        boolean isOnline
) {}

package com.sprint.mission.discodeit.service.dto.user;

public record UserInfoWithStatus(
        String userName,
        String email,
        boolean isOnline
) {}

package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateInfo(
        String userName,
        String password,
        String email,
        UUID profileId,
        byte[] profileImage
) {}

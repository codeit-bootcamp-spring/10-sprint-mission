package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateDTO(
        UUID userId,
        String username,
        UUID userStatusId,
        UUID profileImageId
) { }

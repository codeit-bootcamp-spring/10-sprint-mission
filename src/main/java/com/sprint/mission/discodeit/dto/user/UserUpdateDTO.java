package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.UUID;

public record UserUpdateDTO(
        UUID userId,
        String username,
        UserStatusType statusType,
        UUID profileImageId
) { }

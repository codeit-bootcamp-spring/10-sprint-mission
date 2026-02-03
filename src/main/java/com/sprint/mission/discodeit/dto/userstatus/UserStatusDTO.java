package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserStatusDTO(
        UUID userStatusId,
        UserStatus userStatus
) {}

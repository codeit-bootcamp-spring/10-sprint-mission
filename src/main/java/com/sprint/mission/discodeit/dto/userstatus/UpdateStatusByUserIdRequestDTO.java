package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.UUID;

public record UpdateStatusByUserIdRequestDTO(
        UUID userId,
        UserStatusType statusType
) { }

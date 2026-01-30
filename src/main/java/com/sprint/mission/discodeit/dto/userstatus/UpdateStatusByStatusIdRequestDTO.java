package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.UUID;

public record UpdateStatusByStatusIdRequestDTO(
        UUID userStatusId,
        UserStatusType statusType
) { }

package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.StatusType;

import java.util.UUID;

public record UserStatusUpdateByIdDto(
        UUID id,
        StatusType statusType
) {
}

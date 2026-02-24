package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatusType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateStatusByStatusIdRequestDTO(
        @NotNull(message = "userStatusId는 null일 수 없습니다.")
        UUID userStatusId,
        UserStatusType statusType
) { }

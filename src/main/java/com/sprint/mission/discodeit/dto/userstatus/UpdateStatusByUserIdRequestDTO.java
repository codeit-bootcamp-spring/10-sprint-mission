package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatusType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateStatusByUserIdRequestDTO(
        @NotNull(message = "userId는 null일 수 없습니다.")
        UUID userId,
        UserStatusType statusType
) { }

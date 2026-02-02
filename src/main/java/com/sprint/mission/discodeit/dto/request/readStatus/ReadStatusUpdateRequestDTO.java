package com.sprint.mission.discodeit.dto.request.readStatus;

import com.sprint.mission.discodeit.entity.ReadStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusUpdateRequestDTO {
    @NotNull
    private UUID id;

    @NotNull
    private ReadStatusType readStatusType;
}

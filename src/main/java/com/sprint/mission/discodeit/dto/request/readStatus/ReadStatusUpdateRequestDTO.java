package com.sprint.mission.discodeit.dto.request.readStatus;

import com.sprint.mission.discodeit.entity.ReadStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusUpdateRequestDTO {
    @NotNull
    private UUID id;

    @NotNull
    private ReadStatusType readStatusType;
}

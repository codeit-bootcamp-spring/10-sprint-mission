package com.sprint.mission.discodeit.dto.request.readStatus;

import com.sprint.mission.discodeit.entity.ReadStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusUpdateRequestDTO {
    @Setter
    @NotNull
    private UUID id;

    @NotNull
    private ReadStatusType readStatusType;
}

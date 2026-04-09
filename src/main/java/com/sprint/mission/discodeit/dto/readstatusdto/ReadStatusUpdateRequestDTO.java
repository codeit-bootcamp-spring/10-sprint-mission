package com.sprint.mission.discodeit.dto.readstatusdto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.LastModifiedDate;

public record ReadStatusUpdateRequestDTO(
    @NotNull
    Instant newLastReadAt) {

}

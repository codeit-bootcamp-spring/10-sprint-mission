package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest (
        @NotNull(message = "ID가 null입니다.")
        UUID readStatusId,

        @NotNull(message = "lastReadTime이 null로 입력되었습니다.")
        Instant lastReadTime
){
}

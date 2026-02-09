package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.ReadStatusType;

import java.util.UUID;

public record ReadStatusResponse(
        UUID id,
        ReadStatusType readStatusType
) {
    public static ReadStatusResponse from(
            ReadStatus readStatus
    ) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getReadStatusType()
        );
    }
}

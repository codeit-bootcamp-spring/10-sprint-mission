package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID readStatusId,
        boolean readNow
) {
    public void validate() {
        if (readStatusId == null) throw new IllegalArgumentException("readStatusId는 null일 수 없습니다.");

    }
}

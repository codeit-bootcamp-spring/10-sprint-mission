package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId

) {
    public void validate() {
        if (userId == null) throw new IllegalArgumentException("userId는 null 일 수 없습니다.");
        if (channelId == null) throw new IllegalArgumentException("channelId는 null 일 수 없습니다.");
       }
}

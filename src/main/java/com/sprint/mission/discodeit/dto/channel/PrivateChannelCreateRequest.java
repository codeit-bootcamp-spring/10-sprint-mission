package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.utils.Validation;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> participantUserIds
) {
    public void validate(){
        if (participantUserIds == null || participantUserIds.isEmpty()) {
            throw new IllegalArgumentException("유저 목록이 비어있습니다.");
        }

    }
}

package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.UserStatusType;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UserStatusUpdateRequestDTO {
    private UserStatusType userStatusType;
    private Instant lastOnlineTime;
}

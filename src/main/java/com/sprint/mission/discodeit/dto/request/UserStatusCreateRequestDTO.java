package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.UserStatusType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserStatusCreateRequestDTO {
    private UUID userId;
    private UserStatusType userStatusType;
}

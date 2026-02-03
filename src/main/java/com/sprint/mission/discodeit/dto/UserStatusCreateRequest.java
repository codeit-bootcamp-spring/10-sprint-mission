package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import java.util.UUID;

@Getter
public class UserStatusCreateRequest {
    private final UUID userId;

    public UserStatusCreateRequest(UUID userId) {
        this.userId = userId;
    }
}

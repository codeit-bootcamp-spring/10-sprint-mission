package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserStatusCreateRequest {
    private UUID userId;

    public UserStatusCreateRequest(UUID userId) {
        this.userId = userId;
    }
}

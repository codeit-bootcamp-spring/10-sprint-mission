package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.ToString;
import java.util.UUID;

@Getter
@ToString
public class UserResponse {
    private final UUID id;
    private final String name;
    private final String email;
    private final boolean isOnline;
    private final UUID profileId;

    public UserResponse(UUID id, String name, String email, boolean isOnline, UUID profileId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isOnline = isOnline;
        this.profileId = profileId;
    }
}

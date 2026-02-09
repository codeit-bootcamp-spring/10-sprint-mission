package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private boolean isOnline;
    private UUID profileId;

    public UserResponse(UUID id, String name, String email, boolean isOnline, UUID profileId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isOnline = isOnline;
        this.profileId = profileId;
    }
}

package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private String profileId;
    private boolean isOnline;

    public UserResponseDto(User user, boolean isOnline) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profileId = user.getProfileId();
        this.isOnline = isOnline;
    }
}

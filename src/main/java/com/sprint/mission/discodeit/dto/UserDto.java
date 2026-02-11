package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserDto { // 유저 응답 dto
    private UUID id;
    private String username;
    private String email;
    private boolean isOnline;
    private UUID profileImageId;

    public UserDto(User user, boolean isOnline) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profileImageId = user.getProfileImageId();
        this.isOnline = isOnline;
    }

}

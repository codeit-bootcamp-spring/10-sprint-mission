package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateDto {
    private String name;
    private String email;
    private UUID profileImageId;
    private String password;
}

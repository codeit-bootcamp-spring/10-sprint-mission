package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreateDto {
    private String name;
    private String email;
    private String password;
    private UUID profileImageId;
}

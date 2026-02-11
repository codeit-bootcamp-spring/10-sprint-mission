package com.sprint.mission.discodeit.dto.user;

public record UserCreateInfo(
        String userName,
        String password,
        String email,
        byte[] profileImage
) {}

package com.sprint.mission.discodeit.user.dto;

public record UserCreateInfo(
        String userName,
        String password,
        String email,
        byte[] profileImage
) {}

package com.sprint.mission.discodeit.service.dto.user;

public record UserCreateInfo(
        String userName,
        String password,
        String email,
        byte[] profileImage
) {}

package com.sprint.mission.discodeit.dto.user.request;

import java.util.UUID;

public record UserCreateRequest(
        String name,
        String email,
        String password,
        byte[] profileImage,
        String type
) {}

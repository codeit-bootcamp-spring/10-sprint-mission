package com.sprint.mission.discodeit.dto.user.request;

public record UserCreateRequest(
        String name,
        String email,
        String password
) {}

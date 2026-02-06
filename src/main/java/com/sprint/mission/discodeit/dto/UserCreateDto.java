package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserCreateDto(
        String userName,
        String email,
        String password,
        UUID profileId
        ) {}

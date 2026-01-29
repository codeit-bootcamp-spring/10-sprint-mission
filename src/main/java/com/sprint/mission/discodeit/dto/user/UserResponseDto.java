package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserResponseDto(

    UUID userId,
    String userName,
    String email,
    boolean online


) {}




package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record UserDTO(
        UUID userId,
        User user
) {}

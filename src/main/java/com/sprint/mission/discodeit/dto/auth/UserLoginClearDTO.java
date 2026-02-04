package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;

import java.util.List;
import java.util.UUID;

public record UserLoginClearDTO(
        UUID userId,
        String name,
        String email
) {}

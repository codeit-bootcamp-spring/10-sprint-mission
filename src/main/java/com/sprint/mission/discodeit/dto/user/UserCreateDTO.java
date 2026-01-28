package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDTO;
import jakarta.annotation.Nullable;

public record UserCreateDTO(
        String email,
        String username,
        String password,
        @Nullable
        BinaryContentCreateDTO profileImage
) {}
package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDTO;
import jakarta.annotation.Nullable;

public record CreateUserRequestDTO(
        String email,
        String username,
        String password,
        @Nullable
        BinaryContentCreateDTO profileImage
) {}
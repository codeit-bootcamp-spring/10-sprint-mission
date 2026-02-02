package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.entity.UserStatusType;
import jakarta.annotation.Nullable;

import java.util.UUID;

public record UpdateUserRequestDTO(
        UUID userId,
        String username,
        UserStatusType statusType,
        @Nullable
        CreateBinaryContentPayloadDTO profileImage
) { }

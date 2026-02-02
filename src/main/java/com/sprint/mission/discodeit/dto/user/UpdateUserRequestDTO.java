package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.UUID;

public record UpdateUserRequestDTO(
        UUID userId,
        String username,
        UserStatusType statusType,
        CreateBinaryContentRequestDTO profileImage
) { }

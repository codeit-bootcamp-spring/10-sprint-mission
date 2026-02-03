package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String userName,
        String email,
        boolean isConnected,
        BinaryContentDto profileImage
) {
}

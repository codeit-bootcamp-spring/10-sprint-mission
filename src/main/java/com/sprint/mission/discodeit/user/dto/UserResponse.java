package com.sprint.mission.discodeit.user.dto;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String userName,
        String email,
        boolean isConnected,
        BinaryContentResponse profileImage
) {
}

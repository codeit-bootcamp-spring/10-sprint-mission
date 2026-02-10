package com.sprint.mission.discodeit.user.dto;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        String username,
        String email,
        String password,
        BinaryContentResponse profileImage
) {
}

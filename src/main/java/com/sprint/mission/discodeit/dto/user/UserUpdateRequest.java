package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        String newPassword,
        String newNickname,
        BinaryContentCreateRequest newProfile
) {
}

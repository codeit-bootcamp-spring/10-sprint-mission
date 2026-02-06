package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;

import java.util.UUID;

public record UserUpdateRequest(
        String newPassword,
        String newNickname,
        BinaryContentCreateRequest newProfile
) {
}

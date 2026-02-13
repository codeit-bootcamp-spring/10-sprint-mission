package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;

public record UserUpdateRequest(
        String newUsername,
        String newEmail,
        String newPassword,
        BinaryContentCreateRequest newProfile
) {
}

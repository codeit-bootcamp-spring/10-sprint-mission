package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;

public record UserUpdateRequest(
        String newUsername,
        String newEmail,
        String newPassword,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
) {
}

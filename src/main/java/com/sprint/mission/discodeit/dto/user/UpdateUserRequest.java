package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;

public record UpdateUserRequest(
        String username,
        String password,
        String email,
        BinaryContentRequest profileImage
) {
}

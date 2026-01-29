package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String email,
        String password,
        String userName,
        String nickName,
        String birthday,
        BinaryContentCreateRequest profileImage
) {
}

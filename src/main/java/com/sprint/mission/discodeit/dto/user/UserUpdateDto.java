package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateDto(
        UUID userId,
        String email,
        String password,
        String userName,
        String nickName,
        String birthday,
        byte[] binaryContent
) {
}

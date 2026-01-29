package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;

import java.util.UUID;

public record UserUpdateDto(
        UUID userId,
        String email,
        String password,
        String userName,
        String nickName,
        String birthday,
        BinaryContentCreateDto profileImage
) {
}

package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;

public record UserCreateDto(
        String email,
        String userName,
        String nickName,
        String password,
        String birthday,
        BinaryContentCreateDto profileImage
) {
}

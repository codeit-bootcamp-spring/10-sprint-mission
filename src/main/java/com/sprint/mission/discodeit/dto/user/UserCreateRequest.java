package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;

public record UserCreateRequest(
        String email,
        String userName,
        String nickName,
        String password,
        String birthday,
        BinaryContentCreateRequest profileImage
) {
}

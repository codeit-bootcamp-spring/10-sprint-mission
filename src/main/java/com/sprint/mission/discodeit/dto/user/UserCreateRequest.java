package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;

public record UserCreateRequest(
        String email,
        String password,
        String nickname,
        BinaryContentCreateRequest profile
) {
}

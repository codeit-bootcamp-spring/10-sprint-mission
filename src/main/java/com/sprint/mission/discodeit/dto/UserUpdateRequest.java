package com.sprint.mission.discodeit.dto;

import java.util.UUID;


//유저 정보 수정 용
public record UserUpdateRequest(
        UUID userId,
        String userName,
        String alias,
        String email,
        String password,
        BinaryContentCreateRequest newProfileImage
) {
}

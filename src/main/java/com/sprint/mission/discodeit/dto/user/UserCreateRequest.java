package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

// 유저 생성 시 필요한 데이터
public record UserCreateRequest(
        String name,
        String nickname,
        String email,
        String password,
        UUID profileImageId
) {
}

package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

// 유저 정보 수정 시 필요한 데이터
public record UserUpdateRequest(
        UUID id,
        String name,
        String nickname,
        String email,
        UUID profileImageId
) {
}

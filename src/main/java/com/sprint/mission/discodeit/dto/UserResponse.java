package com.sprint.mission.discodeit.dto;

import java.util.UUID;

// 유저 조회 결과 반환용 -> find/findAll/create,update 반화값
public record UserResponse(
        UUID id,
        String userName,
        String alias,
        String email,
        boolean online,
        UUID profileId
) {
}

package com.sprint.mission.discodeit.dto.user;

// 로그인 시 필요한 데이터
public record LoginRequest(
        String email,
        String password
) {
}

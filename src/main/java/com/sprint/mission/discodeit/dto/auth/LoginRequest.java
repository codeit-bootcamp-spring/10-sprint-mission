package com.sprint.mission.discodeit.dto.auth;

// 로그인 시 필요한 데이터
public record LoginRequest(
        String name,
        String password
) {
}

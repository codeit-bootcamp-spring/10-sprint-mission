package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.dto.user.UserDto;

// 인증 토큰 발급 결과를 담는 DTO
public record AuthTokenResult(
    String accessToken,
    String refreshToken,
    UserDto userDto
) {

}

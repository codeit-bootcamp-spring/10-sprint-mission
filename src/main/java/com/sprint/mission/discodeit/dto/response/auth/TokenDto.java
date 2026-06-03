package com.sprint.mission.discodeit.dto.response.auth;

import lombok.Builder;

@Builder
public record TokenDto(
        JwtDto jwtDto,
        String refreshToken
) {
}

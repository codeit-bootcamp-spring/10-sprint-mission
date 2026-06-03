package com.sprint.mission.discodeit.dto.auth;

public record JwtRefreshDto(

        JwtDto jwtDto,
        String newRefreshToken
) {
}

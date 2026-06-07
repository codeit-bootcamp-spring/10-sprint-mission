package com.sprint.mission.discodeit.dto;

public record JwtRefreshResultDto(
        UserDto userDto,
        String accessToken,
        String refreshToken
) {

}

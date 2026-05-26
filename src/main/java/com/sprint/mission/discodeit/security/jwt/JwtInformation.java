package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.user.UserDto;

public record JwtInformation(
        UserDto userDto,
        String accessToken,
        String refreshToken
) {
    public JwtInformation rotate(String accessToken, String refreshToken) {
        return new JwtInformation(userDto, accessToken, refreshToken);
    }
}

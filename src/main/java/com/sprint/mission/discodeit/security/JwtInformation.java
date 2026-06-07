package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.UserDto;
import lombok.Getter;

@Getter
public class JwtInformation {

    private final UserDto userDto;
    private volatile String accessToken;
    private volatile String refreshToken;

    public JwtInformation(UserDto userDto, String accessToken, String refreshToken) {
        this.userDto = userDto;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public synchronized void rotate(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

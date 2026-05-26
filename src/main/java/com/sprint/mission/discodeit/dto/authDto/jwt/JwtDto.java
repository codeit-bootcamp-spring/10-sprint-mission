package com.sprint.mission.discodeit.dto.authDto.jwt;

import com.sprint.mission.discodeit.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtDto {
    private UserDto userDto;

    private String accessToken;
}

package com.sprint.mission.discodeit.auth.jwt.dto;

import com.sprint.mission.discodeit.dto.user.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {

}

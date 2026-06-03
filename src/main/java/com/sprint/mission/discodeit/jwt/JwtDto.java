package com.sprint.mission.discodeit.jwt;

import com.sprint.mission.discodeit.user.dto.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {

}

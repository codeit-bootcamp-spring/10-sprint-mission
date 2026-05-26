package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.dto.user.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {

}

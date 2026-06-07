package com.sprint.mission.discodeit.auth.dto;

import com.sprint.mission.discodeit.dto.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {

}

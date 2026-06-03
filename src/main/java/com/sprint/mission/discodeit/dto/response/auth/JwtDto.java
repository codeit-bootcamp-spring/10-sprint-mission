package com.sprint.mission.discodeit.dto.response.auth;

import com.sprint.mission.discodeit.dto.response.UserDto;
import lombok.Builder;

@Builder
public record JwtDto (
        UserDto userDto,
        String accessToken
) {
}

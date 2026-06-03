package com.sprint.mission.discodeit.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;

public record JwtDto(
        UserDTO userDTO,
        String accessToken,
        @JsonIgnore String refreshToken
) {
}

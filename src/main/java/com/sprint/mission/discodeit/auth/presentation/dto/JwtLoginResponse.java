package com.sprint.mission.discodeit.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import lombok.Builder;

@Builder
public record JwtLoginResponse(UserResponse userDto,
                               String accessToken,
                               @JsonIgnore String refreshToken) {

}

package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record JwtDto(
        @Schema(description = "사용자 정보") UserDto userDto,
        @Schema(description = "액세스 토큰") String accessToken
) {

}

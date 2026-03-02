package com.sprint.mission.discodeit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 정보")
public record LoginRequest(
        @Schema(description = "로그인할 username", example = "dustle")
        String username,
        @Schema(description = "로그인 비밀번호", example = "1234")
        String password
) {
}

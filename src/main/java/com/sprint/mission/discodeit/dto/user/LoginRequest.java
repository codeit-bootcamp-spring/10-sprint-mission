package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 로그인 요청")
public record LoginRequest(
    @Schema(description = "유저 이름", example = "woody")
    String username,
    @Schema(description = "유저 비밀번호", example = "1234")
    String password
) {

}

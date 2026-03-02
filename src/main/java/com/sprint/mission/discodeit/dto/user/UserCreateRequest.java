package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User 생성 정보")
public record UserCreateRequest(
        @Schema(description = "User 이름", example = "dustle")
        String username,
        @Schema(description = "User 이메일", example = "dustle@email.com")
        String email,
        @Schema(description = "User 비밀번호", example = "1234")
        String password
) {}

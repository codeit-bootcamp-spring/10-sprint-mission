package com.sprint.mission.discodeit.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "사용자 이름을 입력해주세요.")
        String username,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}

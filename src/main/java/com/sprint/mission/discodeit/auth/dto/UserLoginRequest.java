package com.sprint.mission.discodeit.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 정보")
public record UserLoginRequest(
    @NotBlank(message = "유저 네임은 비어 있을 수 없습니다.")
    String username,
    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    String password
) {

}
